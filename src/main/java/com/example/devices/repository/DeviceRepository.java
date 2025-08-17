package com.example.devices.repository;

import com.example.devices.domain.Device;
import com.example.devices.domain.DeviceState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DeviceRepository extends JpaRepository<Device, UUID> {
    List<Device> findByBrandIgnoreCaseContaining(String brand);
    List<Device> findByState(DeviceState state);
}
