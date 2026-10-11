package com.qease.dto;

import com.qease.entity.TokenStatus;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class QueueDto {

    public static class TakeTokenRequest {
        @NotNull(message = "Service ID is required")
        private Long serviceId;

        public TakeTokenRequest() {}
        public TakeTokenRequest(Long serviceId) {
            this.serviceId = serviceId;
        }

        public Long getServiceId() { return serviceId; }
        public void setServiceId(Long serviceId) { this.serviceId = serviceId; }
    }

    public static class QueueTokenDto {
        private Long id;
        private String tokenNumber;
        private int sequenceNumber;
        private Long studentId;
        private String studentName;
        private String studentEmail;
        private Long serviceId;
        private String serviceName;
        private String serviceCode;
        private int serviceEstimatedTimeMinutes;
        private Long counterId;
        private String counterNumber;
        private String counterName;
        private TokenStatus status;
        private long queuePosition;
        private long peopleAhead;
        private int estimatedWaitTimeMinutes;
        private LocalDateTime issueTime;
        private LocalDateTime calledTime;
        private LocalDateTime servedTime;
        private LocalDateTime completedTime;
        private LocalDateTime cancelledTime;
        private String notes;

        public QueueTokenDto() {}

        public QueueTokenDto(Long id, String tokenNumber, int sequenceNumber, Long studentId, String studentName, String studentEmail, Long serviceId, String serviceName, String serviceCode, int serviceEstimatedTimeMinutes, Long counterId, String counterNumber, String counterName, TokenStatus status, long queuePosition, long peopleAhead, int estimatedWaitTimeMinutes, LocalDateTime issueTime, LocalDateTime calledTime, LocalDateTime servedTime, LocalDateTime completedTime, LocalDateTime cancelledTime, String notes) {
            this.id = id;
            this.tokenNumber = tokenNumber;
            this.sequenceNumber = sequenceNumber;
            this.studentId = studentId;
            this.studentName = studentName;
            this.studentEmail = studentEmail;
            this.serviceId = serviceId;
            this.serviceName = serviceName;
            this.serviceCode = serviceCode;
            this.serviceEstimatedTimeMinutes = serviceEstimatedTimeMinutes;
            this.counterId = counterId;
            this.counterNumber = counterNumber;
            this.counterName = counterName;
            this.status = status;
            this.queuePosition = queuePosition;
            this.peopleAhead = peopleAhead;
            this.estimatedWaitTimeMinutes = estimatedWaitTimeMinutes;
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
        public Long getStudentId() { return studentId; }
        public void setStudentId(Long studentId) { this.studentId = studentId; }
        public String getStudentName() { return studentName; }
        public void setStudentName(String studentName) { this.studentName = studentName; }
        public String getStudentEmail() { return studentEmail; }
        public void setStudentEmail(String studentEmail) { this.studentEmail = studentEmail; }
        public Long getServiceId() { return serviceId; }
        public void setServiceId(Long serviceId) { this.serviceId = serviceId; }
        public String getServiceName() { return serviceName; }
        public void setServiceName(String serviceName) { this.serviceName = serviceName; }
        public String getServiceCode() { return serviceCode; }
        public void setServiceCode(String serviceCode) { this.serviceCode = serviceCode; }
        public int getServiceEstimatedTimeMinutes() { return serviceEstimatedTimeMinutes; }
        public void setServiceEstimatedTimeMinutes(int serviceEstimatedTimeMinutes) { this.serviceEstimatedTimeMinutes = serviceEstimatedTimeMinutes; }
        public Long getCounterId() { return counterId; }
        public void setCounterId(Long counterId) { this.counterId = counterId; }
        public String getCounterNumber() { return counterNumber; }
        public void setCounterNumber(String counterNumber) { this.counterNumber = counterNumber; }
        public String getCounterName() { return counterName; }
        public void setCounterName(String counterName) { this.counterName = counterName; }
        public TokenStatus getStatus() { return status; }
        public void setStatus(TokenStatus status) { this.status = status; }
        public long getQueuePosition() { return queuePosition; }
        public void setQueuePosition(long queuePosition) { this.queuePosition = queuePosition; }
        public long getPeopleAhead() { return peopleAhead; }
        public void setPeopleAhead(long peopleAhead) { this.peopleAhead = peopleAhead; }
        public int getEstimatedWaitTimeMinutes() { return estimatedWaitTimeMinutes; }
        public void setEstimatedWaitTimeMinutes(int estimatedWaitTimeMinutes) { this.estimatedWaitTimeMinutes = estimatedWaitTimeMinutes; }
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

        public static Builder builder() { return new Builder(); }
        public static class Builder {
            private Long id;
            private String tokenNumber;
            private int sequenceNumber;
            private Long studentId;
            private String studentName;
            private String studentEmail;
            private Long serviceId;
            private String serviceName;
            private String serviceCode;
            private int serviceEstimatedTimeMinutes;
            private Long counterId;
            private String counterNumber;
            private String counterName;
            private TokenStatus status;
            private long queuePosition;
            private long peopleAhead;
            private int estimatedWaitTimeMinutes;
            private LocalDateTime issueTime;
            private LocalDateTime calledTime;
            private LocalDateTime servedTime;
            private LocalDateTime completedTime;
            private LocalDateTime cancelledTime;
            private String notes;

            public Builder id(Long id) { this.id = id; return this; }
            public Builder tokenNumber(String tokenNumber) { this.tokenNumber = tokenNumber; return this; }
            public Builder sequenceNumber(int sequenceNumber) { this.sequenceNumber = sequenceNumber; return this; }
            public Builder studentId(Long studentId) { this.studentId = studentId; return this; }
            public Builder studentName(String studentName) { this.studentName = studentName; return this; }
            public Builder studentEmail(String studentEmail) { this.studentEmail = studentEmail; return this; }
            public Builder serviceId(Long serviceId) { this.serviceId = serviceId; return this; }
            public Builder serviceName(String serviceName) { this.serviceName = serviceName; return this; }
            public Builder serviceCode(String serviceCode) { this.serviceCode = serviceCode; return this; }
            public Builder serviceEstimatedTimeMinutes(int serviceEstimatedTimeMinutes) { this.serviceEstimatedTimeMinutes = serviceEstimatedTimeMinutes; return this; }
            public Builder counterId(Long counterId) { this.counterId = counterId; return this; }
            public Builder counterNumber(String counterNumber) { this.counterNumber = counterNumber; return this; }
            public Builder counterName(String counterName) { this.counterName = counterName; return this; }
            public Builder status(TokenStatus status) { this.status = status; return this; }
            public Builder queuePosition(long queuePosition) { this.queuePosition = queuePosition; return this; }
            public Builder peopleAhead(long peopleAhead) { this.peopleAhead = peopleAhead; return this; }
            public Builder estimatedWaitTimeMinutes(int estimatedWaitTimeMinutes) { this.estimatedWaitTimeMinutes = estimatedWaitTimeMinutes; return this; }
            public Builder issueTime(LocalDateTime issueTime) { this.issueTime = issueTime; return this; }
            public Builder calledTime(LocalDateTime calledTime) { this.calledTime = calledTime; return this; }
            public Builder servedTime(LocalDateTime servedTime) { this.servedTime = servedTime; return this; }
            public Builder completedTime(LocalDateTime completedTime) { this.completedTime = completedTime; return this; }
            public Builder cancelledTime(LocalDateTime cancelledTime) { this.cancelledTime = cancelledTime; return this; }
            public Builder notes(String notes) { this.notes = notes; return this; }

            public QueueTokenDto build() {
                return new QueueTokenDto(id, tokenNumber, sequenceNumber, studentId, studentName, studentEmail, serviceId, serviceName, serviceCode, serviceEstimatedTimeMinutes, counterId, counterNumber, counterName, status, queuePosition, peopleAhead, estimatedWaitTimeMinutes, issueTime, calledTime, servedTime, completedTime, cancelledTime, notes);
            }
        }
    }

    public static class NotificationDto {
        private Long id;
        private Long studentId;
        private Long tokenId;
        private String title;
        private String message;
        private boolean isRead;
        private LocalDateTime createdAt;

        public NotificationDto() {}
        public NotificationDto(Long id, Long studentId, Long tokenId, String title, String message, boolean isRead, LocalDateTime createdAt) {
            this.id = id;
            this.studentId = studentId;
            this.tokenId = tokenId;
            this.title = title;
            this.message = message;
            this.isRead = isRead;
            this.createdAt = createdAt;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public Long getStudentId() { return studentId; }
        public void setStudentId(Long studentId) { this.studentId = studentId; }
        public Long getTokenId() { return tokenId; }
        public void setTokenId(Long tokenId) { this.tokenId = tokenId; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public boolean isRead() { return isRead; }
        public void setRead(boolean read) { isRead = read; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

        public static Builder builder() { return new Builder(); }
        public static class Builder {
            private Long id;
            private Long studentId;
            private Long tokenId;
            private String title;
            private String message;
            private boolean isRead;
            private LocalDateTime createdAt;

            public Builder id(Long id) { this.id = id; return this; }
            public Builder studentId(Long studentId) { this.studentId = studentId; return this; }
            public Builder tokenId(Long tokenId) { this.tokenId = tokenId; return this; }
            public Builder title(String title) { this.title = title; return this; }
            public Builder message(String message) { this.message = message; return this; }
            public Builder isRead(boolean isRead) { this.isRead = isRead; return this; }
            public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

            public NotificationDto build() {
                return new NotificationDto(id, studentId, tokenId, title, message, isRead, createdAt);
            }
        }
    }
}
