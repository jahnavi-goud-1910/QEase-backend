package com.qease.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "queue_tokens")
public class QueueToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "token_number", nullable = false, length = 30)
    private String tokenNumber;

    @Column(name = "sequence_number", nullable = false)
    private int sequenceNumber;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "service_id", nullable = false)
    private ServiceEntity service;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "counter_id")
    private Counter counter;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TokenStatus status = TokenStatus.WAITING;

    @Column(name = "issue_time", nullable = false)
    private LocalDateTime issueTime;

    @Column(name = "called_time")
    private LocalDateTime calledTime;

    @Column(name = "served_time")
    private LocalDateTime servedTime;

    @Column(name = "completed_time")
    private LocalDateTime completedTime;

    @Column(name = "cancelled_time")
    private LocalDateTime cancelledTime;

    @Column(columnDefinition = "TEXT")
    private String notes;

    public QueueToken() {}

    public QueueToken(Long id, String tokenNumber, int sequenceNumber, User student, ServiceEntity service, Counter counter, TokenStatus status, LocalDateTime issueTime, LocalDateTime calledTime, LocalDateTime servedTime, LocalDateTime completedTime, LocalDateTime cancelledTime, String notes) {
        this.id = id;
        this.tokenNumber = tokenNumber;
        this.sequenceNumber = sequenceNumber;
        this.student = student;
        this.service = service;
        this.counter = counter;
        this.status = status;
        this.issueTime = issueTime;
        this.calledTime = calledTime;
        this.servedTime = servedTime;
        this.completedTime = completedTime;
        this.cancelledTime = cancelledTime;
        this.notes = notes;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTokenNumber() { return tokenNumber; }
    public void setTokenNumber(String tokenNumber) { this.tokenNumber = tokenNumber; }

    public int getSequenceNumber() { return sequenceNumber; }
    public void setSequenceNumber(int sequenceNumber) { this.sequenceNumber = sequenceNumber; }

    public User getStudent() { return student; }
    public void setStudent(User student) { this.student = student; }

    public ServiceEntity getService() { return service; }
    public void setService(ServiceEntity service) { this.service = service; }

    public Counter getCounter() { return counter; }
    public void setCounter(Counter counter) { this.counter = counter; }

    public TokenStatus getStatus() { return status; }
    public void setStatus(TokenStatus status) { this.status = status; }

    public LocalDateTime getIssueTime() { return issueTime; }
    public void setIssueTime(LocalDateTime issueTime) { this.issueTime = issueTime; }

    public LocalDateTime getCalledTime() { return calledTime; }
    public void setCalledTime(LocalDateTime calledTime) { this.calledTime = calledTime; }

    public LocalDateTime getServedTime() { return servedTime; }
    public void setServedTime(LocalDateTime servedTime) { this.servedTime = servedTime; }

    public LocalDateTime getCompletedTime() { return completedTime; }
    public void setCompletedTime(LocalDateTime completedTime) { this.completedTime = completedTime; }

    public LocalDateTime getCancelledTime() { return cancelledTime; }
    public void setCancelledTime(LocalDateTime cancelledTime) { this.cancelledTime = cancelledTime; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String tokenNumber;
        private int sequenceNumber;
        private User student;
        private ServiceEntity service;
        private Counter counter;
        private TokenStatus status = TokenStatus.WAITING;
        private LocalDateTime issueTime;
        private LocalDateTime calledTime;
        private LocalDateTime servedTime;
        private LocalDateTime completedTime;
        private LocalDateTime cancelledTime;
        private String notes;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder tokenNumber(String tokenNumber) { this.tokenNumber = tokenNumber; return this; }
        public Builder sequenceNumber(int sequenceNumber) { this.sequenceNumber = sequenceNumber; return this; }
        public Builder student(User student) { this.student = student; return this; }
        public Builder service(ServiceEntity service) { this.service = service; return this; }
        public Builder counter(Counter counter) { this.counter = counter; return this; }
        public Builder status(TokenStatus status) { this.status = status; return this; }
        public Builder issueTime(LocalDateTime issueTime) { this.issueTime = issueTime; return this; }
        public Builder calledTime(LocalDateTime calledTime) { this.calledTime = calledTime; return this; }
        public Builder servedTime(LocalDateTime servedTime) { this.servedTime = servedTime; return this; }
        public Builder completedTime(LocalDateTime completedTime) { this.completedTime = completedTime; return this; }
        public Builder cancelledTime(LocalDateTime cancelledTime) { this.cancelledTime = cancelledTime; return this; }
        public Builder notes(String notes) { this.notes = notes; return this; }

        public QueueToken build() {
            return new QueueToken(id, tokenNumber, sequenceNumber, student, service, counter, status, issueTime, calledTime, servedTime, completedTime, cancelledTime, notes);
        }
    }
}
