package com.qease.service;

import com.qease.dto.AdminDto;
import com.qease.dto.AuthDto;
import com.qease.dto.CounterDto;
import com.qease.dto.ServiceDto;
import com.qease.entity.*;
import com.qease.exception.BadRequestException;
import com.qease.exception.ResourceNotFoundException;
import com.qease.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private CounterRepository counterRepository;

    @Autowired
    private QueueTokenRepository queueTokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthService authService;

    // --- Dashboard Summary ---
    public AdminDto.AdminDashboardSummaryDto getDashboardSummary() {
        long totalStudents = userRepository.findByRole(Role.STUDENT).size();
        long totalStaff = userRepository.findByRole(Role.STAFF).size();
        long activeServices = serviceRepository.findByActiveTrue().size();
        long activeCounters = counterRepository.findByActiveTrue().size();
        long waitingTokens = queueTokenRepository.countByStatus(TokenStatus.WAITING);
        long currentlyServing = queueTokenRepository.countByStatus(TokenStatus.SERVING);
        long completedTokens = queueTokenRepository.countByStatus(TokenStatus.COMPLETED);
        long cancelledTokens = queueTokenRepository.countByStatus(TokenStatus.CANCELLED);

        return AdminDto.AdminDashboardSummaryDto.builder()
                .totalStudents(totalStudents)
                .totalStaff(totalStaff)
                .activeServices(activeServices)
                .activeCounters(activeCounters)
                .waitingTokens(waitingTokens)
                .currentlyServing(currentlyServing)
                .completedTokens(completedTokens)
                .cancelledTokens(cancelledTokens)
                .build();
    }

    // --- User Management ---
    public List<AuthDto.UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(authService::mapToUserDto)
                .collect(Collectors.toList());
    }

    public List<AuthDto.UserDto> getAllStaff() {
        return userRepository.findByRole(Role.STAFF).stream()
                .map(authService::mapToUserDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public AuthDto.UserDto addStaff(AuthDto.RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already exists: " + request.getEmail());
        }

        User staff = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword() != null ? request.getPassword() : "staff123"))
                .phone(request.getPhone())
                .role(Role.STAFF)
                .active(true)
                .build();

        User saved = userRepository.save(staff);
        return authService.mapToUserDto(saved);
    }

    @Transactional
    public AuthDto.UserDto toggleUserStatus(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
        user.setActive(!user.isActive());
        return authService.mapToUserDto(userRepository.save(user));
    }

    // --- Service Management ---
    public List<ServiceDto> getAllServices() {
        return serviceRepository.findAll().stream()
                .map(this::mapServiceToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ServiceDto createService(ServiceDto dto) {
        if (serviceRepository.existsByCode(dto.getCode())) {
            throw new BadRequestException("Service code already exists: " + dto.getCode());
        }

        ServiceEntity service = ServiceEntity.builder()
                .name(dto.getName())
                .code(dto.getCode().toUpperCase())
                .description(dto.getDescription())
                .estimatedTimeMinutes(dto.getEstimatedTimeMinutes() > 0 ? dto.getEstimatedTimeMinutes() : 10)
                .active(true)
                .build();

        return mapServiceToDto(serviceRepository.save(service));
    }

    @Transactional
    public ServiceDto updateService(Long serviceId, ServiceDto dto) {
        ServiceEntity service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found: " + serviceId));

        service.setName(dto.getName());
        service.setDescription(dto.getDescription());
        service.setEstimatedTimeMinutes(dto.getEstimatedTimeMinutes());
        return mapServiceToDto(serviceRepository.save(service));
    }

    @Transactional
    public ServiceDto toggleServiceStatus(Long serviceId) {
        ServiceEntity service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found: " + serviceId));
        service.setActive(!service.isActive());
        return mapServiceToDto(serviceRepository.save(service));
    }

    // --- Counter Management ---
    public List<CounterDto> getAllCounters() {
        return counterRepository.findAll().stream()
                .map(this::mapCounterToDto)
                .collect(Collectors.toList());
    }

    /**
     * Resolves the staff user for a counter assignment and guarantees one staff member never ends up on
     * two counters (which would make "my counter" ambiguous).
     */
    private User resolveStaffForCounter(Long staffId, Long counterIdBeingEdited) {
        if (staffId == null) {
            return null;
        }
        User staff = userRepository.findById(staffId)
                .orElseThrow(() -> new ResourceNotFoundException("Staff user not found: " + staffId));
        if (staff.getRole() != Role.STAFF) {
            throw new BadRequestException(staff.getFullName() + " is not a staff account and cannot be assigned to a counter.");
        }
        for (Counter other : counterRepository.findAllByStaffId(staffId)) {
            if (counterIdBeingEdited == null || !other.getId().equals(counterIdBeingEdited)) {
                throw new BadRequestException(staff.getFullName() + " is already assigned to counter " + other.getCounterNumber() + ".");
            }
        }
        return staff;
    }

    @Transactional
    public CounterDto createCounter(CounterDto dto) {
        if (counterRepository.existsByCounterNumber(dto.getCounterNumber())) {
            throw new BadRequestException("Counter number already exists: " + dto.getCounterNumber());
        }

        ServiceEntity service = null;
        if (dto.getServiceId() != null) {
            service = serviceRepository.findById(dto.getServiceId()).orElse(null);
        }

        User staff = resolveStaffForCounter(dto.getStaffId(), null);

        Counter counter = Counter.builder()
                .counterNumber(dto.getCounterNumber())
                .name(dto.getName())
                .service(service)
                .staff(staff)
                .active(true)
                .build();

        return mapCounterToDto(counterRepository.save(counter));
    }

    @Transactional
    public CounterDto updateCounter(Long counterId, CounterDto dto) {
        Counter counter = counterRepository.findById(counterId)
                .orElseThrow(() -> new ResourceNotFoundException("Counter not found: " + counterId));

        counter.setName(dto.getName());
        if (dto.getServiceId() != null) {
            counter.setService(serviceRepository.findById(dto.getServiceId()).orElse(null));
        } else {
            counter.setService(null);
        }

        counter.setStaff(resolveStaffForCounter(dto.getStaffId(), counterId));

        return mapCounterToDto(counterRepository.save(counter));
    }

    @Transactional
    public CounterDto toggleCounterStatus(Long counterId) {
        Counter counter = counterRepository.findById(counterId)
                .orElseThrow(() -> new ResourceNotFoundException("Counter not found: " + counterId));
        counter.setActive(!counter.isActive());
        return mapCounterToDto(counterRepository.save(counter));
    }

    public ServiceDto mapServiceToDto(ServiceEntity service) {
        long waitingCount = queueTokenRepository.countByServiceAndStatus(service, TokenStatus.WAITING);
        return ServiceDto.builder()
                .id(service.getId())
                .name(service.getName())
                .code(service.getCode())
                .description(service.getDescription())
                .estimatedTimeMinutes(service.getEstimatedTimeMinutes())
                .active(service.isActive())
                .waitingCount(waitingCount)
                .build();
    }

    public CounterDto mapCounterToDto(Counter counter) {
        long waitingCount = 0;
        if (counter.getService() != null) {
            waitingCount = queueTokenRepository.countByServiceAndStatus(counter.getService(), TokenStatus.WAITING);
        }

        return CounterDto.builder()
                .id(counter.getId())
                .counterNumber(counter.getCounterNumber())
                .name(counter.getName())
                .serviceId(counter.getService() != null ? counter.getService().getId() : null)
                .serviceName(counter.getService() != null ? counter.getService().getName() : "Unassigned")
                .serviceCode(counter.getService() != null ? counter.getService().getCode() : "N/A")
                .staffId(counter.getStaff() != null ? counter.getStaff().getId() : null)
                .staffName(counter.getStaff() != null ? counter.getStaff().getFullName() : "Unassigned")
                .active(counter.isActive())
                .waitingCount(waitingCount)
                .build();
    }
}
