package com.qease.controller;

import com.qease.dto.ServiceDto;
import com.qease.repository.ServiceRepository;
import com.qease.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/services")
public class ServiceController {

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private AdminService adminService;

    @GetMapping
    public ResponseEntity<List<ServiceDto>> getActiveServices() {
        List<ServiceDto> services = serviceRepository.findByActiveTrue().stream()
                .map(adminService::mapServiceToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(services);
    }
}
