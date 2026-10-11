package com.qease.repository;

import com.qease.entity.Counter;
import com.qease.entity.ServiceEntity;
import com.qease.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CounterRepository extends JpaRepository<Counter, Long> {
    Optional<Counter> findByCounterNumber(String counterNumber);
    Optional<Counter> findByStaffId(Long staffId);
    Optional<Counter> findByStaff(User staff);
    /** Deterministic lookup (lowest id first) so legacy duplicate assignments can never crash the staff portal. */
    Optional<Counter> findFirstByStaffIdOrderByIdAsc(Long staffId);
    List<Counter> findAllByStaffId(Long staffId);
    List<Counter> findByService(ServiceEntity service);
    List<Counter> findByActiveTrue();
    Boolean existsByCounterNumber(String counterNumber);
}
