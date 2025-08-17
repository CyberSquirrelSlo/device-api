package com.example.devices.dto;

import com.example.devices.domain.DeviceState;
import java.time.Instant;
import java.util.UUID;

public record DeviceResponse(
        UUID id,
        String name,
        String brand,
        DeviceState state,
        Instant createdAt
) {}
