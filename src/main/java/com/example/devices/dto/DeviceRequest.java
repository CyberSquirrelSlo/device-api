package com.example.devices.dto;

import com.example.devices.domain.DeviceState;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DeviceRequest(
        @NotBlank String name,
        @NotBlank String brand,
        @NotNull DeviceState state
) {}
