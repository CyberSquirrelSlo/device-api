package com.example.devices.mapper;

import com.example.devices.domain.Device;
import com.example.devices.dto.DeviceRequest;
import com.example.devices.dto.DeviceResponse;
import com.example.devices.dto.DeviceUpdateRequest;

public class DeviceMapper {

    public static Device toEntity(DeviceRequest request) {
        return Device.builder()
                .name(request.name())
                .brand(request.brand())
                .state(request.state())
                .build();
    }

    public static void updateEntity(Device device, DeviceUpdateRequest request) {
        if (request.name() != null) {
            device.setName(request.name());
        }
        if (request.brand() != null) {
            device.setBrand(request.brand());
        }
        if (request.state() != null) {
            device.setState(request.state());
        }
    }

    public static DeviceResponse toResponse(Device device) {
        return new DeviceResponse(
                device.getId(),
                device.getName(),
                device.getBrand(),
                device.getState(),
                device.getCreatedAt()
        );
    }
}
