package com.qease.controller;

import com.qease.dto.CounterDto;
import com.qease.dto.QueueDto;
import com.qease.security.UserDetailsImpl;
import com.qease.service.StaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Staff portal API. The staff member is always the authenticated principal (resolved from the JWT);
 * no staff, counter or service identifier is ever accepted from the client for the "my counter" endpoints.
 */
@RestController
@RequestMapping("/api/staff")
public class StaffController {

    @Autowired
    private StaffService staffService;

    @GetMapping("/counter")
    public ResponseEntity<CounterDto> getAssignedCounter(@AuthenticationPrincipal UserDetailsImpl principal) {
        return ResponseEntity.ok(staffService.getAssignedCounterForStaff(principal.getId()));
    }

    @GetMapping("/queue")
    public ResponseEntity<List<QueueDto.QueueTokenDto>> getWaitingQueue(@AuthenticationPrincipal UserDetailsImpl principal) {
        return ResponseEntity.ok(staffService.getWaitingQueueForStaff(principal.getId()));
    }

    @GetMapping("/current")
    public ResponseEntity<QueueDto.QueueTokenDto> getCurrentToken(@AuthenticationPrincipal UserDetailsImpl principal) {
        return ResponseEntity.ok(staffService.getCurrentTokenForStaff(principal.getId()));
    }

    @PostMapping("/queue/next")
    public ResponseEntity<QueueDto.QueueTokenDto> callNextStudent(@AuthenticationPrincipal UserDetailsImpl principal) {
        return ResponseEntity.ok(staffService.callNextStudent(principal.getId()));
    }

    @PostMapping("/queue/{id}/serve")
    public ResponseEntity<QueueDto.QueueTokenDto> startServing(@PathVariable Long id,
                                                               @AuthenticationPrincipal UserDetailsImpl principal) {
        return ResponseEntity.ok(staffService.startServing(id, principal.getId()));
    }

    @PostMapping("/queue/{id}/complete")
    public ResponseEntity<QueueDto.QueueTokenDto> completeToken(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> body,
            @AuthenticationPrincipal UserDetailsImpl principal) {
        String notes = (body != null) ? body.get("notes") : null;
        return ResponseEntity.ok(staffService.completeToken(id, notes, principal.getId()));
    }

    @PostMapping("/queue/{id}/skip")
    public ResponseEntity<QueueDto.QueueTokenDto> skipToken(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> body,
            @AuthenticationPrincipal UserDetailsImpl principal) {
        String notes = (body != null) ? body.get("notes") : null;
        return ResponseEntity.ok(staffService.skipToken(id, notes, principal.getId()));
    }
}
