package com.qease.dto;

import jakarta.validation.constraints.NotBlank;

public class CounterDto {

    private Long id;

    @NotBlank(message = "Counter number is required")
    private String counterNumber;

    @NotBlank(message = "Counter name is required")
    private String name;

    private Long serviceId;
    private String serviceName;
    private String serviceCode;

    private Long staffId;
    private String staffName;

    private boolean active = true;

    private String currentTokenNumber;
    private Long waitingCount;

    public CounterDto() {}

    public CounterDto(Long id, String counterNumber, String name, Long serviceId, String serviceName, String serviceCode, Long staffId, String staffName, boolean active, String currentTokenNumber, Long waitingCount) {
        this.id = id;
        this.counterNumber = counterNumber;
        this.name = name;
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.serviceCode = serviceCode;
        this.staffId = staffId;
        this.staffName = staffName;
        this.active = active;
        this.currentTokenNumber = currentTokenNumber;
        this.waitingCount = waitingCount;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCounterNumber() { return counterNumber; }
    public void setCounterNumber(String counterNumber) { this.counterNumber = counterNumber; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Long getServiceId() { return serviceId; }
    public void setServiceId(Long serviceId) { this.serviceId = serviceId; }

    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }

    public String getServiceCode() { return serviceCode; }
    public void setServiceCode(String serviceCode) { this.serviceCode = serviceCode; }

    public Long getStaffId() { return staffId; }
    public void setStaffId(Long staffId) { this.staffId = staffId; }

    public String getStaffName() { return staffName; }
    public void setStaffName(String staffName) { this.staffName = staffName; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public String getCurrentTokenNumber() { return currentTokenNumber; }
    public void setCurrentTokenNumber(String currentTokenNumber) { this.currentTokenNumber = currentTokenNumber; }

    public Long getWaitingCount() { return waitingCount; }
    public void setWaitingCount(Long waitingCount) { this.waitingCount = waitingCount; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private String counterNumber;
        private String name;
        private Long serviceId;
        private String serviceName;
        private String serviceCode;
        private Long staffId;
        private String staffName;
        private boolean active = true;
        private String currentTokenNumber;
        private Long waitingCount;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder counterNumber(String counterNumber) { this.counterNumber = counterNumber; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder serviceId(Long serviceId) { this.serviceId = serviceId; return this; }
        public Builder serviceName(String serviceName) { this.serviceName = serviceName; return this; }
        public Builder serviceCode(String serviceCode) { this.serviceCode = serviceCode; return this; }
        public Builder staffId(Long staffId) { this.staffId = staffId; return this; }
        public Builder staffName(String staffName) { this.staffName = staffName; return this; }
        public Builder active(boolean active) { this.active = active; return this; }
        public Builder currentTokenNumber(String currentTokenNumber) { this.currentTokenNumber = currentTokenNumber; return this; }
        public Builder waitingCount(Long waitingCount) { this.waitingCount = waitingCount; return this; }

        public CounterDto build() {
            return new CounterDto(id, counterNumber, name, serviceId, serviceName, serviceCode, staffId, staffName, active, currentTokenNumber, waitingCount);
        }
    }
}
