package com.qease;

import com.qease.entity.Counter;
import com.qease.entity.Role;
import com.qease.entity.ServiceEntity;
import com.qease.repository.CounterRepository;
import com.qease.repository.ServiceRepository;
import com.qease.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/** The seeded demo data must give EVERY service its own staffed counter, with no staff on two counters. */
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
public class DataInitializerTest {

    @Autowired private ServiceRepository serviceRepository;
    @Autowired private CounterRepository counterRepository;
    @Autowired private UserRepository userRepository;

    @Test
    void everySeededServiceHasAStaffedCounter() {
        List<ServiceEntity> services = serviceRepository.findAll();
        assertTrue(services.size() >= 6);
        for (ServiceEntity service : services) {
            boolean staffed = counterRepository.findByService(service).stream()
                    .anyMatch(c -> c.getStaff() != null && c.getStaff().getRole() == Role.STAFF);
            assertTrue(staffed, "No staffed counter for service " + service.getCode());
        }
    }

    @Test
    void noStaffMemberIsOnTwoCounters() {
        Set<Long> seen = new HashSet<>();
        for (Counter c : counterRepository.findAll()) {
            if (c.getStaff() != null) {
                assertTrue(seen.add(c.getStaff().getId()), "Staff on two counters: " + c.getStaff().getEmail());
            }
        }
    }

    @Test
    void unassignedDemoStaffExists() {
        var none = userRepository.findByEmail("staff.none@college.edu").orElseThrow();
        assertTrue(counterRepository.findAllByStaffId(none.getId()).isEmpty());
    }
}
