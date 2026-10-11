package com.qease;

import com.qease.dto.AuthDto;
import com.qease.entity.Role;
import com.qease.repository.UserRepository;
import com.qease.service.AuthService;
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
public class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void testRegisterAndLoginStudent() {
        AuthDto.RegisterRequest regReq = new AuthDto.RegisterRequest(
                "John Doe",
                "john.doe@qease.com",
                "password123",
                "+1234567890",
                Role.STUDENT
        );

        AuthDto.UserDto userDto = authService.register(regReq);
        assertNotNull(userDto);
        assertEquals("John Doe", userDto.getFullName());
        assertEquals("john.doe@qease.com", userDto.getEmail());
        assertEquals(Role.STUDENT, userDto.getRole());

        // Now test login
        AuthDto.LoginRequest loginReq = new AuthDto.LoginRequest("john.doe@qease.com", "password123");
        AuthDto.JwtResponse jwtResponse = authService.login(loginReq);

        assertNotNull(jwtResponse);
        assertNotNull(jwtResponse.getToken());
        assertEquals("Bearer", jwtResponse.getType());
        assertEquals("john.doe@qease.com", jwtResponse.getEmail());
        assertEquals(Role.STUDENT, jwtResponse.getRole());
    }
}
