package com.example.devices;


import com.example.devices.domain.DeviceState;
import com.example.devices.dto.DeviceResponse;
import com.example.devices.service.DeviceService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Adjust the import to your controller class package/name:
@WebMvcTest(controllers = com.example.devices.controller.DeviceController.class)
class DeviceControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    DeviceService service;



    @Test
    void getById_returns200() throws Exception {
        UUID id = UUID.randomUUID();
        DeviceResponse resp = new DeviceResponse(id, "MBP", "Apple",
                DeviceState.IN_USE, Instant.now());

        Mockito.when(service.findById(id)).thenReturn(resp);

        mvc.perform(get("/api/devices/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("MBP"));
    }

    @Test
    void getAll_returns200_list() throws Exception {
        DeviceResponse a = new DeviceResponse(UUID.randomUUID(), "MBP", "Apple",
                DeviceState.IN_USE, Instant.now());
        DeviceResponse b = new DeviceResponse(UUID.randomUUID(), "ThinkPad", "Lenovo",
                DeviceState.AVAILABLE, Instant.now());

        Mockito.when(service.findAll()).thenReturn(List.of(a, b));

        mvc.perform(get("/api/devices"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void findByBrand_returns200() throws Exception {
        Mockito.when(service.findByBrand("apple")).thenReturn(List.of(
                new DeviceResponse(UUID.randomUUID(), "iPhone", "Apple", DeviceState.AVAILABLE, Instant.now())
        ));

        mvc.perform(get("/api/devices/brand/{brand}", "apple"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].brand").value("Apple"));
    }

    @Test
    void findByState_returns200() throws Exception {
        Mockito.when(service.findByState(DeviceState.INACTIVE)).thenReturn(List.of());

        mvc.perform(get("/api/devices/state/{state}", "INACTIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void update_returns200() throws Exception {
        UUID id = UUID.randomUUID();
        DeviceResponse updated = new DeviceResponse(id, "XPS 13", "Dell",
                DeviceState.AVAILABLE, Instant.now());

        Mockito.when(service.update(eq(id), any())).thenReturn(updated);

        String json = """
          {
            "name":"XPS 13",
            "brand":"Dell",
            "state":"AVAILABLE"
          }
        """;

        mvc.perform(put("/api/devices/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("XPS 13"))
                .andExpect(jsonPath("$.brand").value("Dell"));
    }

    @Test
    void delete_returns204() throws Exception {
        UUID id = UUID.randomUUID();
        Mockito.doNothing().when(service).delete(id);

        mvc.perform(delete("/api/devices/{id}", id))
                .andExpect(status().isNoContent());
    }
}
