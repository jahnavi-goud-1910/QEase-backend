package com.qease.repository;

import com.qease.entity.ServiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceRepository extends JpaRepository<ServiceEntity, Long> {
    Optional<ServiceEntity> findByCode(String code);
    Optional<ServiceEntity> findByName(String name);
    Boolean existsByName(String name);
    Boolean existsByCode(String code);
    List<ServiceEntity> findByActiveTrue();
}
