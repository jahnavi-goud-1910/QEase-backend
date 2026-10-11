package com.qease;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qease.entity.*;
import com.qease.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * End-to-end through the real security filter chain: log in via /api/auth/login, then call the staff
 * endpoints with the returned JWT. Proves the dashboard data is derived from whoever is authenticated.
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
public class StaffApiTest {

    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper json;
    @Autowired private PasswordEncoder encoder;
    @Autowired private UserRepository userRepository;
    @Autowired private ServiceRepository serviceRepository;
    @Autowired private CounterRepository counterRepository;
    @Autowired private QueueTokenRepository queueTokenRepository;
    @Autowired private NotificationRepository notificationRepository;

    private User alpha, beta, nobody, student;
    private ServiceEntity acct, exam;

    private User user(String name, String email, Role role) {
        return userRepository.save(User.builder().fullName(name).email(email).password(encoder.encode("pw12345")).role(role).active(true).build());
    }

    @BeforeEach
    void setUp() {
        notificationRepository.deleteAll();
        queueTokenRepository.deleteAll();
        counterRepository.deleteAll();
        userRepository.deleteAll();
        serviceRepository.deleteAll();

        alpha = user("Staff Alpha", "alpha@qease.com", Role.STAFF);
        beta = user("Staff Beta", "beta@qease.com", Role.STAFF);
        nobody = user("Staff Nobody", "nobody@qease.com", Role.STAFF);
        student = user("Alice Student", "alice@qease.com", Role.STUDENT);

        acct = serviceRepository.save(ServiceEntity.builder().name("Accounts / Fee Payment").code("ACCT").estimatedTimeMinutes(10).active(true).build());
        exam = serviceRepository.save(ServiceEntity.builder().name("Examination Cell").code("EXAM").estimatedTimeMinutes(8).active(true).build());

        counterRepository.save(Counter.builder().counterNumber("C-201").name("Accounts Desk").service(acct).staff(alpha).active(true).build());
        counterRepository.save(Counter.builder().counterNumber("C-202").name("Exam Window").service(exam).staff(beta).active(true).build());
    }

    private String login(String email) throws Exception {
        String body = mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + email + "\",\"password\":\"pw12345\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        JsonNode node = json.readTree(body);
        return "Bearer " + node.get("token").asText();
    }

    @Test
    void differentLoginsGetDifferentCounters() throws Exception {
        String alphaToken = login("alpha@qease.com");
        String betaToken = login("beta@qease.com");

        mvc.perform(get("/api/staff/counter").header("Authorization", alphaToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.staffName").value("Staff Alpha"))
                .andExpect(jsonPath("$.staffId").value(alpha.getId()))
                .andExpect(jsonPath("$.counterNumber").value("C-201"))
                .andExpect(jsonPath("$.serviceCode").value("ACCT"));

        mvc.perform(get("/api/staff/counter").header("Authorization", betaToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.staffName").value("Staff Beta"))
                .andExpect(jsonPath("$.staffId").value(beta.getId()))
                .andExpect(jsonPath("$.counterNumber").value("C-202"))
                .andExpect(jsonPath("$.serviceCode").value("EXAM"));
    }

    @Test
    void staffWithoutCounterGets404WithClearMessage() throws Exception {
        mvc.perform(get("/api/staff/counter").header("Authorization", login("nobody@qease.com")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("No counter assigned. Please contact the administrator."));

        mvc.perform(get("/api/staff/queue").header("Authorization", login("nobody@qease.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void unauthenticatedAndWrongRoleAreRejected() throws Exception {
        mvc.perform(get("/api/staff/counter")).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/staff/counter").header("Authorization", login("alice@qease.com")))
                .andExpect(status().isForbidden());
    }

    @Test
    void waitingTokenAppearsOnlyOnMatchingStaffQueueAndOtherStaffGets403OnForeignToken() throws Exception {
        String studentToken = login("alice@qease.com");
        mvc.perform(post("/api/queue/take").header("Authorization", studentToken)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"serviceId\":" + acct.getId() + "}"))
                .andExpect(status().isOk());

        String alphaToken = login("alpha@qease.com");
        String betaToken = login("beta@qease.com");

        mvc.perform(get("/api/staff/queue").header("Authorization", alphaToken))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].tokenNumber").value("ACCT-001"));
        mvc.perform(get("/api/staff/queue").header("Authorization", betaToken))
                .andExpect(jsonPath("$.length()").value(0));

        String called = mvc.perform(post("/api/staff/queue/next").header("Authorization", alphaToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CALLED"))
                .andReturn().getResponse().getContentAsString();
        long tokenId = json.readTree(called).get("id").asLong();

        mvc.perform(post("/api/staff/queue/" + tokenId + "/complete").header("Authorization", betaToken))
                .andExpect(status().isForbidden());

        mvc.perform(post("/api/staff/queue/" + tokenId + "/complete").header("Authorization", alphaToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));

        assertEquals(TokenStatus.COMPLETED, queueTokenRepository.findById(tokenId).orElseThrow().getStatus());
    }
}
