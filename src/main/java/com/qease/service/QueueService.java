package com.qease.service;

import com.qease.dto.QueueDto;
import com.qease.entity.*;
import com.qease.exception.BadRequestException;
import com.qease.exception.ResourceNotFoundException;
import com.qease.repository.QueueTokenRepository;
import com.qease.repository.ServiceRepository;
import com.qease.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class QueueService {

    @Autowired
    private QueueTokenRepository queueTokenRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationService notificationService;

    @Transactional
    public QueueDto.QueueTokenDto takeToken(String studentEmail, Long serviceId) {
        User student = userRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with email: " + studentEmail));

        ServiceEntity service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with id: " + serviceId));

        if (!service.isActive()) {
            throw new BadRequestException("This college service is currently inactive and not accepting new queue tokens.");
        }

        // Verify student does not already have an active token for any service
        List<TokenStatus> activeStatuses = Arrays.asList(TokenStatus.WAITING, TokenStatus.CALLED, TokenStatus.SERVING);
        Optional<QueueToken> existingActiveToken = queueTokenRepository.findFirstByStudentAndStatusInOrderByIssueTimeDesc(student, activeStatuses);
        if (existingActiveToken.isPresent()) {
            QueueToken token = existingActiveToken.get();
            throw new BadRequestException("You already have an active token (" + token.getTokenNumber() + 
                    " for " + token.getService().getName() + "). Please complete or cancel your existing token before taking a new one.");
        }

        // Calculate next sequence number for this service
        Integer maxSeq = queueTokenRepository.findMaxSequenceNumberByService(service);
        int nextSeq = (maxSeq == null) ? 1 : maxSeq + 1;
        String tokenNumber = String.format("%s-%03d", service.getCode(), nextSeq);

        QueueToken newToken = QueueToken.builder()
                .tokenNumber(tokenNumber)
                .sequenceNumber(nextSeq)
                .student(student)
                .service(service)
                .status(TokenStatus.WAITING)
                .issueTime(LocalDateTime.now())
                .build();

        QueueToken savedToken = queueTokenRepository.save(newToken);

        QueueDto.QueueTokenDto dto = mapToDto(savedToken);

        // Check if student's turn is approaching right away
        if (dto.getPeopleAhead() <= 2) {
            notificationService.createQueueNotification(
                    student,
                    savedToken,
                    "Turn Approaching!",
                    String.format("Your token %s for %s is close to being called. There are %d student(s) ahead of you.",
                            tokenNumber, service.getName(), dto.getPeopleAhead())
            );
        }

        return dto;
    }

    public QueueDto.QueueTokenDto getActiveTokenForStudent(String studentEmail) {
        User student = userRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        List<TokenStatus> activeStatuses = Arrays.asList(TokenStatus.WAITING, TokenStatus.CALLED, TokenStatus.SERVING);
        Optional<QueueToken> activeToken = queueTokenRepository.findFirstByStudentAndStatusInOrderByIssueTimeDesc(student, activeStatuses);

        return activeToken.map(this::mapToDto).orElse(null);
    }

    public List<QueueDto.QueueTokenDto> getStudentHistory(String studentEmail) {
        User student = userRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        return queueTokenRepository.findByStudentOrderByIssueTimeDesc(student)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public QueueDto.QueueTokenDto getTokenDetails(Long tokenId) {
        QueueToken token = queueTokenRepository.findById(tokenId)
                .orElseThrow(() -> new ResourceNotFoundException("Token not found with id: " + tokenId));
        return mapToDto(token);
    }

    @Transactional
    public QueueDto.QueueTokenDto cancelToken(Long tokenId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        QueueToken token = queueTokenRepository.findById(tokenId)
                .orElseThrow(() -> new ResourceNotFoundException("Token not found with id: " + tokenId));

        if (!token.getStudent().getId().equals(user.getId()) && user.getRole() != Role.ADMIN) {
            throw new BadRequestException("You are not authorized to cancel this token.");
        }

        if (token.getStatus() == TokenStatus.COMPLETED || token.getStatus() == TokenStatus.CANCELLED) {
            throw new BadRequestException("Token is already " + token.getStatus().name().toLowerCase() + ".");
        }

        token.setStatus(TokenStatus.CANCELLED);
        token.setCancelledTime(LocalDateTime.now());
        QueueToken updated = queueTokenRepository.save(token);

        notificationService.createQueueNotification(
                token.getStudent(),
                updated,
                "Token Cancelled",
                "Your token " + token.getTokenNumber() + " has been successfully cancelled."
        );

        return mapToDto(updated);
    }

    public QueueDto.QueueTokenDto mapToDto(QueueToken token) {
        long peopleAhead = 0;
        long queuePosition = 0;
        int estimatedWaitTime = 0;

        if (token.getStatus() == TokenStatus.WAITING) {
            peopleAhead = queueTokenRepository.countPeopleAheadInQueue(token.getService(), token.getIssueTime());
            queuePosition = peopleAhead + 1;
            estimatedWaitTime = (int) (peopleAhead * token.getService().getEstimatedTimeMinutes());
        } else if (token.getStatus() == TokenStatus.CALLED || token.getStatus() == TokenStatus.SERVING) {
            peopleAhead = 0;
            queuePosition = 1;
            estimatedWaitTime = 0;
        }

        return QueueDto.QueueTokenDto.builder()
                .id(token.getId())
                .tokenNumber(token.getTokenNumber())
                .sequenceNumber(token.getSequenceNumber())
                .studentId(token.getStudent().getId())
                .studentName(token.getStudent().getFullName())
                .studentEmail(token.getStudent().getEmail())
                .serviceId(token.getService().getId())
                .serviceName(token.getService().getName())
                .serviceCode(token.getService().getCode())
                .serviceEstimatedTimeMinutes(token.getService().getEstimatedTimeMinutes())
                .counterId(token.getCounter() != null ? token.getCounter().getId() : null)
                .counterNumber(token.getCounter() != null ? token.getCounter().getCounterNumber() : null)
                .counterName(token.getCounter() != null ? token.getCounter().getName() : null)
                .status(token.getStatus())
                .queuePosition(queuePosition)
                .peopleAhead(peopleAhead)
                .estimatedWaitTimeMinutes(estimatedWaitTime)
                .issueTime(token.getIssueTime())
                .calledTime(token.getCalledTime())
                .servedTime(token.getServedTime())
                .completedTime(token.getCompletedTime())
                .cancelledTime(token.getCancelledTime())
                .notes(token.getNotes())
                .build();
    }
}
