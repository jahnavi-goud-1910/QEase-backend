package com.qease.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "counters")
public class Counter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "counter_number", nullable = false, unique = true, length = 20)
    private String counterNumber;

    @Column(nullable = false, length = 100)
    private String name;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "service_id")
    private ServiceEntity service;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "staff_id")
    private User staff;

    @Column(nullable = false)
    private boolean active = true;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Counter() {}

    public Counter(Long id, String counterNumber, String name, ServiceEntity service, User staff, boolean active) {
        this.id = id;
        this.counterNumber = counterNumber;
        this.name = name;
        this.service = service;
        this.staff = staff;
        this.active = active;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCounterNumber() { return counterNumber; }
    public void setCounterNumber(String counterNumber) { this.counterNumber = counterNumber; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public ServiceEntity getService() { return service; }
    public void setService(ServiceEntity service) { this.service = service; }

    public User getStaff() { return staff; }
    public void setStaff(User staff) { this.staff = staff; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String counterNumber;
        private String name;
        private ServiceEntity service;
        private User staff;
        private boolean active = true;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder counterNumber(String counterNumber) { this.counterNumber = counterNumber; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder service(ServiceEntity service) { this.service = service; return this; }
        public Builder staff(User staff) { this.staff = staff; return this; }
        public Builder active(boolean active) { this.active = active; return this; }

        public Counter build() {
            return new Counter(id, counterNumber, name, service, staff, active);
        }
    }
}
