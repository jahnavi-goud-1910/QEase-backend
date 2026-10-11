package com.qease;

import com.qease.dto.QueueDto;
import com.qease.entity.Role;
import com.qease.entity.ServiceEntity;
import com.qease.entity.TokenStatus;
import com.qease.entity.User;
import com.qease.exception.BadRequestException;
import com.qease.repository.QueueTokenRepository;
import com.qease.repository.ServiceRepository;
import com.qease.repository.UserRepository;
import com.qease.service.QueueService;
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
public class QueueServiceTest {

    @Autowired
    private QueueService queueService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private QueueTokenRepository queueTokenRepository;

    private User testStudent;
    private ServiceEntity testService;

    @BeforeEach
    void setUp() {
        queueTokenRepository.deleteAll();
        userRepository.deleteAll();
        serviceRepository.deleteAll();

        testStudent = User.builder()
                .fullName("Test Student")
                .email("student.test@qease.com")
                .password("$2a$10$8.UnVuG9HHgffUDAlk8qfOUVGkqRzgVymGe07xd00DMxs.AQiy.Si")
                .role(Role.STUDENT)
                .active(true)
                .build();
        userRepository.save(testStudent);

        testService = ServiceEntity.builder()
                .name("Examination Cell")
                .code("EXAM")
                .description("Exam support")
                .estimatedTimeMinutes(10)
                .active(true)
                .build();
        serviceRepository.save(testService);
    }

    @Test
    void testTakeTokenSuccess() {
        QueueDto.QueueTokenDto token = queueService.takeToken(testStudent.getEmail(), testService.getId());

        assertNotNull(token);
        assertEquals("EXAM-001", token.getTokenNumber());
        assertEquals(TokenStatus.WAITING, token.getStatus());
        assertEquals(1, token.getQueuePosition());
        assertEquals(0, token.getPeopleAhead());
        assertEquals(0, token.getEstimatedWaitTimeMinutes());
    }

    @Test
    void testPreventDuplicateActiveToken() {
        // First token succeeds
        queueService.takeToken(testStudent.getEmail(), testService.getId());

        // Attempting to take a second token while first is WAITING must throw BadRequestException
        assertThrows(BadRequestException.class, () -> {
            queueService.takeToken(testStudent.getEmail(), testService.getId());
        });
    }

    @Test
    void testCancelToken() {
        QueueDto.QueueTokenDto token = queueService.takeToken(testStudent.getEmail(), testService.getId());

        QueueDto.QueueTokenDto cancelled = queueService.cancelToken(token.getId(), testStudent.getEmail());

        assertNotNull(cancelled);
        assertEquals(TokenStatus.CANCELLED, cancelled.getStatus());
        assertNotNull(cancelled.getCancelledTime());

        // After cancelling, student can take another token
        QueueDto.QueueTokenDto newToken = queueService.takeToken(testStudent.getEmail(), testService.getId());
        assertNotNull(newToken);
        assertEquals("EXAM-002", newToken.getTokenNumber());
    }
}
