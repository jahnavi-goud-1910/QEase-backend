package com.qease;

import com.qease.dto.CounterDto;
import com.qease.dto.QueueDto;
import com.qease.entity.*;
import com.qease.exception.BadRequestException;
import com.qease.exception.ForbiddenException;
import com.qease.exception.ResourceNotFoundException;
import com.qease.repository.CounterRepository;
import com.qease.repository.NotificationRepository;
import com.qease.repository.QueueTokenRepository;
import com.qease.repository.ServiceRepository;
import com.qease.repository.UserRepository;
import com.qease.service.QueueService;
import com.qease.service.StaffService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
public class StaffServiceTest {

    private static final String HASH = "$2a$10$8.UnVuG9HHgffUDAlk8qfOUVGkqRzgVymGe07xd00DMxs.AQiy.Si";

    @Autowired private StaffService staffService;
    @Autowired private QueueService queueService;
    @Autowired private UserRepository userRepository;
    @Autowired private ServiceRepository serviceRepository;
    @Autowired private CounterRepository counterRepository;
    @Autowired private QueueTokenRepository queueTokenRepository;
    @Autowired private NotificationRepository notificationRepository;

    private User staffA;      // Accounts counter
    private User staffB;      // Exam counter
    private User staffNone;   // no counter
    private User admin;
    private User student1;
    private User student2;
    private ServiceEntity acct;
    private ServiceEntity exam;
    private Counter counterA;
    private Counter counterB;

    private User user(String name, String email, Role role) {
        return userRepository.save(User.builder().fullName(name).email(email).password(HASH).role(role).active(true).build());
    }

    @BeforeEach
    void setUp() {
        notificationRepository.deleteAll();
        queueTokenRepository.deleteAll();
        counterRepository.deleteAll();
        userRepository.deleteAll();
        serviceRepository.deleteAll();

        staffA = user("Staff Alpha", "alpha@qease.com", Role.STAFF);
        staffB = user("Staff Beta", "beta@qease.com", Role.STAFF);
        staffNone = user("Staff Nobody", "nobody@qease.com", Role.STAFF);
        admin = user("Admin", "admin@qease.com", Role.ADMIN);
        student1 = user("Alice Student", "alice@qease.com", Role.STUDENT);
        student2 = user("Bob Student", "bob@qease.com", Role.STUDENT);

        acct = serviceRepository.save(ServiceEntity.builder().name("Accounts / Fee Payment").code("ACCT").estimatedTimeMinutes(10).active(true).build());
        exam = serviceRepository.save(ServiceEntity.builder().name("Examination Cell").code("EXAM").estimatedTimeMinutes(8).active(true).build());

        counterA = counterRepository.save(Counter.builder().counterNumber("C-201").name("Accounts Desk").service(acct).staff(staffA).active(true).build());
        counterB = counterRepository.save(Counter.builder().counterNumber("C-202").name("Exam Window").service(exam).staff(staffB).active(true).build());
    }

    // Scenario 1 + 2: every staff member sees THEIR OWN counter and service
    @Test
    void eachStaffMemberSeesTheirOwnCounterAndService() {
        CounterDto a = staffService.getAssignedCounterForStaff(staffA.getId());
        assertEquals("C-201", a.getCounterNumber());
        assertEquals("Accounts Desk", a.getName());
        assertEquals("ACCT", a.getServiceCode());
        assertEquals(staffA.getId(), a.getStaffId());
        assertEquals("Staff Alpha", a.getStaffName());

        CounterDto b = staffService.getAssignedCounterForStaff(staffB.getId());
        assertEquals("C-202", b.getCounterNumber());
        assertEquals("Exam Window", b.getName());
        assertEquals("EXAM", b.getServiceCode());
        assertEquals(staffB.getId(), b.getStaffId());
        assertEquals("Staff Beta", b.getStaffName());
    }

    // Scenario 4: no assigned counter -> clear message, never someone else's counter
    @Test
    void staffWithoutCounterGetsClearMessageAndEmptyData() {
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> staffService.getAssignedCounterForStaff(staffNone.getId()));
        assertEquals(StaffService.NO_COUNTER_MESSAGE, ex.getMessage());

        assertTrue(staffService.getWaitingQueueForStaff(staffNone.getId()).isEmpty());
        assertNull(staffService.getCurrentTokenForStaff(staffNone.getId()));
        assertThrows(ResourceNotFoundException.class, () -> staffService.callNextStudent(staffNone.getId()));
    }

    // Scenario 5: a student joins -> the right staff member sees the new waiting count (and only them)
    @Test
    void studentJoiningUpdatesOnlyTheMatchingStaffDashboard() {
        assertEquals(0L, staffService.getAssignedCounterForStaff(staffA.getId()).getWaitingCount());

        queueService.takeToken(student1.getEmail(), acct.getId());

        assertEquals(1L, staffService.getAssignedCounterForStaff(staffA.getId()).getWaitingCount());
        assertEquals(1, staffService.getWaitingQueueForStaff(staffA.getId()).size());
        assertEquals("ACCT-001", staffService.getWaitingQueueForStaff(staffA.getId()).get(0).getTokenNumber());

        assertEquals(0L, staffService.getAssignedCounterForStaff(staffB.getId()).getWaitingCount());
        assertTrue(staffService.getWaitingQueueForStaff(staffB.getId()).isEmpty());
    }

    // Scenario 6 + 7: call next / complete
    @Test
    void callNextThenCompleteUpdatesCurrentTokenAndQueue() {
        queueService.takeToken(student1.getEmail(), acct.getId());
        queueService.takeToken(student2.getEmail(), acct.getId());

        QueueDto.QueueTokenDto called = staffService.callNextStudent(staffA.getId());
        assertEquals("ACCT-001", called.getTokenNumber());
        assertEquals(TokenStatus.CALLED, called.getStatus());
        assertEquals(counterA.getId(), called.getCounterId());

        QueueDto.QueueTokenDto current = staffService.getCurrentTokenForStaff(staffA.getId());
        assertNotNull(current);
        assertEquals("ACCT-001", current.getTokenNumber());
        assertEquals(1, staffService.getWaitingQueueForStaff(staffA.getId()).size());
        assertEquals("ACCT-001", staffService.getAssignedCounterForStaff(staffA.getId()).getCurrentTokenNumber());

        // the other counter is untouched
        assertNull(staffService.getCurrentTokenForStaff(staffB.getId()));

        QueueDto.QueueTokenDto serving = staffService.startServing(called.getId(), staffA.getId());
        assertEquals(TokenStatus.SERVING, serving.getStatus());

        QueueDto.QueueTokenDto done = staffService.completeToken(serving.getId(), "Receipt printed", staffA.getId());
        assertEquals(TokenStatus.COMPLETED, done.getStatus());
        assertNull(staffService.getCurrentTokenForStaff(staffA.getId()));

        QueueDto.QueueTokenDto next = staffService.callNextStudent(staffA.getId());
        assertEquals("ACCT-002", next.getTokenNumber());
        assertEquals(0, staffService.getWaitingQueueForStaff(staffA.getId()).size());
    }

    @Test
    void cannotCallAnotherStudentWhileOneIsAlreadyCalled() {
        queueService.takeToken(student1.getEmail(), acct.getId());
        queueService.takeToken(student2.getEmail(), acct.getId());

        staffService.callNextStudent(staffA.getId());
        assertThrows(BadRequestException.class, () -> staffService.callNextStudent(staffA.getId()));
    }

    @Test
    void skipToken() {
        queueService.takeToken(student1.getEmail(), acct.getId());
        QueueDto.QueueTokenDto called = staffService.callNextStudent(staffA.getId());

        QueueDto.QueueTokenDto skipped = staffService.skipToken(called.getId(), "Student not present", staffA.getId());
        assertEquals(TokenStatus.SKIPPED, skipped.getStatus());
        assertEquals("Student not present", skipped.getNotes());
        assertNull(staffService.getCurrentTokenForStaff(staffA.getId()));
    }

    // Scenario 8: staff cannot manage another counter's tokens
    @Test
    void staffCannotManageAnotherCountersToken() {
        queueService.takeToken(student1.getEmail(), acct.getId());
        QueueDto.QueueTokenDto calledAtA = staffService.callNextStudent(staffA.getId());

        assertThrows(ForbiddenException.class, () -> staffService.startServing(calledAtA.getId(), staffB.getId()));
        assertThrows(ForbiddenException.class, () -> staffService.completeToken(calledAtA.getId(), null, staffB.getId()));
        assertThrows(ForbiddenException.class, () -> staffService.skipToken(calledAtA.getId(), null, staffB.getId()));
        assertThrows(ForbiddenException.class, () -> staffService.completeToken(calledAtA.getId(), null, staffNone.getId()) );

        // token untouched by the rejected attempts
        assertEquals(TokenStatus.CALLED, queueTokenRepository.findById(calledAtA.getId()).orElseThrow().getStatus());
    }

    @Test
    void staffCannotPullTokensFromAnotherServiceQueue() {
        queueService.takeToken(student1.getEmail(), exam.getId());   // belongs to counter B's service

        assertThrows(BadRequestException.class, () -> staffService.callNextStudent(staffA.getId())); // A's queue empty
        assertEquals(TokenStatus.WAITING, queueTokenRepository.findAll().get(0).getStatus());
    }

    @Test
    void adminIsExplicitlyAuthorisedToActOnAnyToken() {
        queueService.takeToken(student1.getEmail(), acct.getId());
        QueueDto.QueueTokenDto calledAtA = staffService.callNextStudent(staffA.getId());

        QueueDto.QueueTokenDto done = staffService.completeToken(calledAtA.getId(), "Closed by admin", admin.getId());
        assertEquals(TokenStatus.COMPLETED, done.getStatus());
    }
}
