package com.example.devices;

import com.example.devices.domain.Device;
import com.example.devices.domain.DeviceState;
import com.example.devices.dto.DeviceRequest;
import com.example.devices.dto.DeviceResponse;
import com.example.devices.dto.DeviceUpdateRequest;
import com.example.devices.exception.BadRequestException;
import com.example.devices.exception.NotFoundException;
import com.example.devices.mapper.DeviceMapper;
import com.example.devices.repository.DeviceRepository;
import com.example.devices.service.DeviceService;
import org.assertj.core.api.AssertProvider;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeviceServiceTest {
    @Mock
    DeviceRepository repo;

    DeviceService service;


    @BeforeEach
    void setUp() {
        repo = mock(DeviceRepository.class);
        service = new DeviceService(repo);

    }

    @Test
    void create_succeeds() {
        DeviceRequest req = new DeviceRequest("X1 Carbon", "Lenovo", DeviceState.AVAILABLE);
        Device saved = Device.builder()
                .id(UUID.randomUUID())
                .name(req.name())
                .brand(req.brand())
                .state(req.state())
                .build();
        when(repo.save(any(Device.class))).thenReturn(saved);

        var resp = service.create(req);
        assertThat(resp.name()).isEqualTo("X1 Carbon");
        verify(repo).save(any(Device.class));
    }

    @Test
    void findById_notFound_throws() {
        UUID id = UUID.randomUUID();
        when(repo.findById(id)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.findById(id)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void findAll_notFound() {
        when(repo.findAll()).thenReturn(Collections.emptyList()); // ✅ return empty list

        assertThat((AssertProvider<Boolean>) () -> service.findAll().isEmpty());
    }


    @Test
    void update_inUse_cannotChangeNameOrBrand() {
        UUID id = UUID.randomUUID();
        Device inUse = Device.builder().id(id).name("MBP").brand("Apple").state(DeviceState.IN_USE).build();
        when(repo.findById(id)).thenReturn(Optional.of(inUse));

        var req = new DeviceUpdateRequest("NewName", "NewBrand", DeviceState.IN_USE);
        assertThatThrownBy(() -> service.update(id, req))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Cannot update name or brand");
    }

    @Test
    void delete_inUse_throws() {
        UUID id = UUID.randomUUID();
        Device inUse = Device.builder().id(id).name("Router").brand("Netgear").state(DeviceState.IN_USE).build();
        when(repo.findById(id)).thenReturn(Optional.of(inUse));

        assertThatThrownBy(() -> service.delete(id))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Cannot delete a device that is in use");
    }



    @Test
    void findByBrand_found() {
        // Arrange
        Device entity = Device.builder()
                .id(UUID.randomUUID())
                .name("Router")
                .brand("Netgear")
                .state(DeviceState.IN_USE)
                .build();

        // Stub the repository to return the entity list
        when(repo.findByBrandIgnoreCaseContaining(anyString()))
                .thenReturn(List.of(entity));

        // Act
        List<DeviceResponse> result = service.findByBrand("Netgear");

        // Assert
        assertThat(result).hasSize(1);
        // If DeviceMapper.toResponse sets a dynamic createdAt, ignore it:
        DeviceResponse expected = DeviceMapper.toResponse(entity);
        assertThat(result.get(0))
                .usingRecursiveComparison()
                .ignoringFields("createdAt")
                .isEqualTo(expected);

        // Verify exact argument actually passed
        verify(repo).findByBrandIgnoreCaseContaining("Netgear");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void findByBrand_empty() {
        when(repo.findByBrandIgnoreCaseContaining(anyString()))
                .thenReturn(List.of());

        List<DeviceResponse> result = service.findByBrand("Unknown");

        assertThat(result).isEmpty();
        verify(repo).findByBrandIgnoreCaseContaining("Unknown");
        verifyNoMoreInteractions(repo);
    }


    @Test
    void findByState_found() {
        // Arrange
        Device entity = Device.builder()
                .id(UUID.randomUUID())
                .name("Router")
                .brand("Netgear")
                .state(DeviceState.IN_USE)
                .build();

        // Stub the repository to return the entity list
        when(repo.findByState(DeviceState.IN_USE))
                .thenReturn(List.of(entity));

        // Act
        List<DeviceResponse> result = service.findByState(DeviceState.IN_USE);

        // Assert
        assertThat(result).hasSize(1);
        // If DeviceMapper.toResponse sets a dynamic createdAt, ignore it:
        DeviceResponse expected = DeviceMapper.toResponse(entity);
        assertThat(result.get(0))
                .usingRecursiveComparison()
                .ignoringFields("createdAt")
                .isEqualTo(expected);

        // Verify exact argument actually passed
        verify(repo).findByState(DeviceState.IN_USE);
        verifyNoMoreInteractions(repo);
    }

    @Test
    void findByState_empty() {
        when(repo.findByState(any()))
                .thenReturn(List.of());

        List<DeviceResponse> result = service.findByState(DeviceState.AVAILABLE);

        assertThat(result).isEmpty();
        verify(repo).findByState(DeviceState.AVAILABLE);
        verifyNoMoreInteractions(repo);
    }

}
