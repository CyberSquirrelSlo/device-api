package com.example.devices.service;

import com.example.devices.domain.Device;
import com.example.devices.domain.DeviceState;
import com.example.devices.dto.DeviceRequest;
import com.example.devices.dto.DeviceResponse;
import com.example.devices.dto.DeviceUpdateRequest;
import com.example.devices.exception.BadRequestException;
import com.example.devices.exception.NotFoundException;
import com.example.devices.mapper.DeviceMapper;
import com.example.devices.repository.DeviceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class DeviceService {

    private final DeviceRepository repository;

    public DeviceService(DeviceRepository repository) {
        this.repository = repository;
    }

    public DeviceResponse create(DeviceRequest request) {
        Device device = DeviceMapper.toEntity(request);
        device.setCreatedAt(Instant.now());
        return DeviceMapper.toResponse(repository.save(device));
    }

    public DeviceResponse findById(UUID id) {
        Device device = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Device not found: " + id));
        return DeviceMapper.toResponse(device);
    }

    public List<DeviceResponse> findAll() {
        return repository.findAll()
                .stream()
                .map(DeviceMapper::toResponse)
                .toList();
    }

    public List<DeviceResponse> findByBrand(String brand) {
        return repository.findByBrandIgnoreCaseContaining(brand)
                .stream()
                .map(DeviceMapper::toResponse)
                .toList();
    }

    public List<DeviceResponse> findByState(DeviceState state) {
        return repository.findByState(state)
                .stream()
                .map(DeviceMapper::toResponse)
                .toList();
    }

    public DeviceResponse update(UUID id, DeviceUpdateRequest request) {
        Device device = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Device not found: " + id));

        if (device.getState() == DeviceState.IN_USE) {
            if (request.name() != null || request.brand() != null) {
                throw new BadRequestException("Cannot update name or brand of a device in use.");
            }
        }

        DeviceMapper.updateEntity(device, request);
        return DeviceMapper.toResponse(repository.save(device));
    }

    public void delete(UUID id) {
        Device device = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Device not found: " + id));

        if (device.getState() == DeviceState.IN_USE) {
            throw new BadRequestException("Cannot delete a device that is in use.");
        }

        repository.delete(device);
    }
}
