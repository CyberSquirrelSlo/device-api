package com.example.devices.dto;

import com.example.devices.domain.DeviceState;

public record DeviceUpdateRequest(
        String name,
        String brand,
        DeviceState state
) {}
