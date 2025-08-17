package com.example.devices.controller;

import com.example.devices.domain.DeviceState;
import com.example.devices.dto.DeviceRequest;
import com.example.devices.dto.DeviceResponse;
import com.example.devices.dto.DeviceUpdateRequest;
import com.example.devices.service.DeviceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/devices")
public class DeviceController {

    private final DeviceService service;

    public DeviceController(DeviceService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<DeviceResponse> create(@Valid @RequestBody DeviceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeviceResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping
    public ResponseEntity<List<DeviceResponse>> getAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/brand/{brand}")
    public ResponseEntity<List<DeviceResponse>> getByBrand(@PathVariable String brand) {
        return ResponseEntity.ok(service.findByBrand(brand));
    }

    @GetMapping("/state/{state}")
    public ResponseEntity<List<DeviceResponse>> getByState(@PathVariable DeviceState state) {
        return ResponseEntity.ok(service.findByState(state));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DeviceResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody DeviceUpdateRequest request
    ) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
