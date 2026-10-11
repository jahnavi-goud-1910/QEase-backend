package com.qease.repository;

import com.qease.entity.Counter;
import com.qease.entity.QueueToken;
import com.qease.entity.ServiceEntity;
import com.qease.entity.TokenStatus;
import com.qease.entity.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface QueueTokenRepository extends JpaRepository<QueueToken, Long> {

    List<QueueToken> findByStudentOrderByIssueTimeDesc(User student);

    List<QueueToken> findByStudentAndStatusIn(User student, List<TokenStatus> statuses);

    Optional<QueueToken> findFirstByStudentAndStatusInOrderByIssueTimeDesc(User student, List<TokenStatus> statuses);

    List<QueueToken> findByServiceAndStatusOrderByIssueTimeAsc(ServiceEntity service, TokenStatus status);

    List<QueueToken> findByServiceAndStatusInOrderByIssueTimeAsc(ServiceEntity service, List<TokenStatus> statuses);

    List<QueueToken> findByCounterAndStatus(Counter counter, TokenStatus status);

    Optional<QueueToken> findFirstByCounterAndStatusOrderByIssueTimeAsc(Counter counter, TokenStatus status);

    Optional<QueueToken> findFirstByServiceAndStatusOrderByIssueTimeAsc(ServiceEntity service, TokenStatus status);

    /** Row-locking variant used when calling the next student so two counters can never grab the same token. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<QueueToken> findFirstByServiceIdAndStatusOrderByIssueTimeAsc(Long serviceId, TokenStatus status);

    List<QueueToken> findByServiceIdAndStatusOrderByIssueTimeAsc(Long serviceId, TokenStatus status);

    long countByServiceIdAndStatus(Long serviceId, TokenStatus status);

    long countByServiceAndStatus(ServiceEntity service, TokenStatus status);

    long countByStatus(TokenStatus status);

    @Query("SELECT COUNT(t) FROM QueueToken t WHERE t.service = :service AND t.status = 'WAITING' AND t.issueTime < :issueTime")
    long countPeopleAheadInQueue(@Param("service") ServiceEntity service, @Param("issueTime") LocalDateTime issueTime);

    @Query("SELECT MAX(t.sequenceNumber) FROM QueueToken t WHERE t.service = :service")
    Integer findMaxSequenceNumberByService(@Param("service") ServiceEntity service);

    List<QueueToken> findByIssueTimeAfter(LocalDateTime after);

    List<QueueToken> findByStatus(TokenStatus status);
}
