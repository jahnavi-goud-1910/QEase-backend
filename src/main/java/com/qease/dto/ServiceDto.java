package com.qease.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ServiceDto {

    private Long id;

    @NotBlank(message = "Service name is required")
    private String name;

    @NotBlank(message = "Service code is required")
    private String code;

    private String description;

    @NotNull(message = "Estimated time is required")
    private Integer estimatedTimeMinutes;

    private boolean active = true;

    private Long waitingCount;

    public ServiceDto() {}

    public ServiceDto(Long id, String name, String code, String description, Integer estimatedTimeMinutes, boolean active, Long waitingCount) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.description = description;
        this.estimatedTimeMinutes = estimatedTimeMinutes;
        this.active = active;
        this.waitingCount = waitingCount;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getEstimatedTimeMinutes() { return estimatedTimeMinutes; }
    public void setEstimatedTimeMinutes(Integer estimatedTimeMinutes) { this.estimatedTimeMinutes = estimatedTimeMinutes; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public Long getWaitingCount() { return waitingCount; }
    public void setWaitingCount(Long waitingCount) { this.waitingCount = waitingCount; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private String name;
        private String code;
        private String description;
        private Integer estimatedTimeMinutes;
        private boolean active = true;
        private Long waitingCount;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder code(String code) { this.code = code; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder estimatedTimeMinutes(Integer estimatedTimeMinutes) { this.estimatedTimeMinutes = estimatedTimeMinutes; return this; }
        public Builder active(boolean active) { this.active = active; return this; }
        public Builder waitingCount(Long waitingCount) { this.waitingCount = waitingCount; return this; }

        public ServiceDto build() {
            return new ServiceDto(id, name, code, description, estimatedTimeMinutes, active, waitingCount);
        }
    }
}
