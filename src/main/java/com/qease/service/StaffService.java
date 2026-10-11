package com.qease.service;

import com.qease.dto.CounterDto;
import com.qease.dto.QueueDto;
import com.qease.entity.*;
import com.qease.exception.BadRequestException;
import com.qease.exception.ForbiddenException;
import com.qease.exception.ResourceNotFoundException;
import com.qease.repository.CounterRepository;
import com.qease.repository.QueueTokenRepository;
import com.qease.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Staff counter operations.
 *
 * Every method identifies the staff member by the authenticated user's ID (taken from the JWT-backed
 * security context by the controller) and resolves the counter through the counters.staff_id column.
 * Nothing here is keyed on a fixed name, counter, or service.
 */
@Service
public class StaffService {

    public static final String NO_COUNTER_MESSAGE = "No counter assigned. Please contact the administrator.";

    @Autowired
    private CounterRepository counterRepository;

    @Autowired
    private QueueTokenRepository queueTokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QueueService queueService;

    @Autowired
    private NotificationService notificationService;

    // ------------------------------------------------------------------ lookups

    private User requireUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Staff user not found: " + userId));
    }

    private Optional<Counter> findCounter(Long userId) {
        return counterRepository.findFirstByStaffIdOrderByIdAsc(userId);
    }

    private Counter requireCounter(Long userId) {
        return findCounter(userId).orElseThrow(() -> new ResourceNotFoundException(NO_COUNTER_MESSAGE));
    }

    private Optional<QueueToken> findActiveToken(Counter counter) {
        Optional<QueueToken> serving = queueTokenRepository
                .findFirstByCounterAndStatusOrderByIssueTimeAsc(counter, TokenStatus.SERVING);
        if (serving.isPresent()) {
            return serving;
        }
        return queueTokenRepository.findFirstByCounterAndStatusOrderByIssueTimeAsc(counter, TokenStatus.CALLED);
    }

    /**
     * Loads a token and verifies the caller may act on it: it must have been called to the caller's own
     * counter. Administrators are explicitly authorised to act on any token.
     */
    private QueueToken requireManageableToken(Long tokenId, Long userId) {
        QueueToken token = queueTokenRepository.findById(tokenId)
                .orElseThrow(() -> new ResourceNotFoundException("Token not found: " + tokenId));

        User user = requireUser(userId);
        if (user.getRole() == Role.ADMIN) {
            return token;
        }

        Counter counter = requireCounter(userId);
        boolean ownsToken = token.getCounter() != null && token.getCounter().getId().equals(counter.getId());
        if (!ownsToken) {
            throw new ForbiddenException("This token is not assigned to your counter (" + counter.getCounterNumber() + ").");
        }
        return token;
    }

    // ------------------------------------------------------------------ read APIs

    public CounterDto getAssignedCounterForStaff(Long userId) {
        User staff = requireUser(userId);
        Counter counter = requireCounter(userId);

        long waitingCount = 0;
        if (counter.getService() != null) {
            waitingCount = queueTokenRepository.countByServiceIdAndStatus(counter.getService().getId(), TokenStatus.WAITING);
        }

        String currentTokenNumber = findActiveToken(counter).map(QueueToken::getTokenNumber).orElse(null);

        return CounterDto.builder()
                .id(counter.getId())
                .counterNumber(counter.getCounterNumber())
                .name(counter.getName())
                .serviceId(counter.getService() != null ? counter.getService().getId() : null)
                .serviceName(counter.getService() != null ? counter.getService().getName() : "Unassigned")
                .serviceCode(counter.getService() != null ? counter.getService().getCode() : "N/A")
                .staffId(staff.getId())
                .staffName(staff.getFullName())
                .active(counter.isActive())
                .currentTokenNumber(currentTokenNumber)
                .waitingCount(waitingCount)
                .build();
    }

    /** Waiting tokens for the caller's own service only. Staff without a counter simply get an empty list. */
    public List<QueueDto.QueueTokenDto> getWaitingQueueForStaff(Long userId) {
        Optional<Counter> counter = findCounter(userId);
        if (counter.isEmpty() || counter.get().getService() == null) {
            return List.of();
        }
        return queueTokenRepository
                .findByServiceIdAndStatusOrderByIssueTimeAsc(counter.get().getService().getId(), TokenStatus.WAITING)
                .stream()
                .map(queueService::mapToDto)
                .collect(Collectors.toList());
    }

    /** The token currently CALLED/SERVING at the caller's counter, or null when the counter is idle. */
    public QueueDto.QueueTokenDto getCurrentTokenForStaff(Long userId) {
        return findCounter(userId)
                .flatMap(this::findActiveToken)
                .map(queueService::mapToDto)
                .orElse(null);
    }

    // ------------------------------------------------------------------ actions

    @Transactional
    public QueueDto.QueueTokenDto callNextStudent(Long userId) {
        Counter counter = requireCounter(userId);

        if (!counter.isActive()) {
            throw new BadRequestException("Counter " + counter.getCounterNumber() + " is not active.");
        }
        if (counter.getService() == null) {
            throw new BadRequestException("Counter " + counter.getCounterNumber() + " has no service assigned.");
        }

        Optional<QueueToken> activeAtCounter = findActiveToken(counter);
        if (activeAtCounter.isPresent()) {
            throw new BadRequestException("Please complete or skip current token ("
                    + activeAtCounter.get().getTokenNumber() + ") before calling next.");
        }

        QueueToken token = queueTokenRepository
                .findFirstByServiceIdAndStatusOrderByIssueTimeAsc(counter.getService().getId(), TokenStatus.WAITING)
                .orElseThrow(() -> new BadRequestException(
                        "The queue is empty! No waiting students for " + counter.getService().getName() + "."));

        token.setStatus(TokenStatus.CALLED);
        token.setCounter(counter);
        token.setCalledTime(LocalDateTime.now());

        QueueToken savedToken = queueTokenRepository.save(token);

        notificationService.createQueueNotification(
                token.getStudent(),
                savedToken,
                "YOUR TURN IS HERE!",
                String.format("Token %s is CALLED to %s (%s). Please proceed to the counter immediately!",
                        token.getTokenNumber(), counter.getName(), counter.getCounterNumber())
        );

        return queueService.mapToDto(savedToken);
    }

    @Transactional
    public QueueDto.QueueTokenDto startServing(Long tokenId, Long userId) {
        QueueToken token = requireManageableToken(tokenId, userId);

        if (token.getStatus() != TokenStatus.CALLED) {
            throw new BadRequestException("Cannot start serving token with status: " + token.getStatus() + ". Must be CALLED first.");
        }

        token.setStatus(TokenStatus.SERVING);
        token.setServedTime(LocalDateTime.now());

        return queueService.mapToDto(queueTokenRepository.save(token));
    }

    @Transactional
    public QueueDto.QueueTokenDto completeToken(Long tokenId, String notes, Long userId) {
        QueueToken token = requireManageableToken(tokenId, userId);

        if (token.getStatus() != TokenStatus.SERVING && token.getStatus() != TokenStatus.CALLED) {
            throw new BadRequestException("Cannot complete token with status: " + token.getStatus());
        }

        token.setStatus(TokenStatus.COMPLETED);
        token.setCompletedTime(LocalDateTime.now());
        if (notes != null && !notes.trim().isEmpty()) {
            token.setNotes(notes);
        }

        QueueToken saved = queueTokenRepository.save(token);

        notificationService.createQueueNotification(
                token.getStudent(),
                saved,
                "Service Completed",
                "Your token " + token.getTokenNumber() + " has been marked as COMPLETED. Thank you!"
        );

        return queueService.mapToDto(saved);
    }

    @Transactional
    public QueueDto.QueueTokenDto skipToken(Long tokenId, String notes, Long userId) {
        QueueToken token = requireManageableToken(tokenId, userId);

        if (token.getStatus() != TokenStatus.CALLED && token.getStatus() != TokenStatus.SERVING) {
            throw new BadRequestException("Cannot skip token with status: " + token.getStatus());
        }

        token.setStatus(TokenStatus.SKIPPED);
        if (notes != null && !notes.trim().isEmpty()) {
            token.setNotes(notes);
        }

        QueueToken saved = queueTokenRepository.save(token);

        notificationService.createQueueNotification(
                token.getStudent(),
                saved,
                "Token Skipped",
                "Your token " + token.getTokenNumber() + " was skipped because you were not present at the counter."
        );

        return queueService.mapToDto(saved);
    }
}
