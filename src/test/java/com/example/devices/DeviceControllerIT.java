package com.example.devices;

import com.example.devices.domain.DeviceState;
import com.example.devices.dto.DeviceRequest;
import com.example.devices.dto.DeviceResponse;
import com.example.devices.dto.DeviceUpdateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DeviceControllerIT {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("devicesdb")
            .withUsername("devices_user")
            .withPassword("devices_pass");

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry r) {
        r.add("spring.datasource.url", postgres::getJdbcUrl);
        r.add("spring.datasource.username", postgres::getUsername);
        r.add("spring.datasource.password", postgres::getPassword);
        r.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        r.add("spring.flyway.enabled", () -> "false");
    }

    @LocalServerPort
    int port;

    @Autowired
    TestRestTemplate rest;

    private String baseUrl() {
        return "http://localhost:" + port + "/api/devices";
    }

    @BeforeEach
    void cleanAndSeed() {
        // There’s no delete-all endpoint, so we rely on create-drop between tests.
        // Seed a few devices that many tests will assert against.
        create("iPhone 15", "Apple", DeviceState.AVAILABLE);
        create("MacBook Pro", "Apple", DeviceState.IN_USE);
        create("ThinkPad", "Lenovo", DeviceState.INACTIVE);
    }

    // ---------- Helpers ----------

    private DeviceResponse create(String name, String brand, DeviceState state) {
        DeviceRequest req = new DeviceRequest(name, brand, state);
        ResponseEntity<DeviceResponse> resp = rest.postForEntity(baseUrl(), req, DeviceResponse.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(resp.getBody()).isNotNull();
        return resp.getBody();
    }

    private DeviceResponse getById(String id) {
        ResponseEntity<DeviceResponse> resp = rest.getForEntity(baseUrl() + "/" + id, DeviceResponse.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody()).isNotNull();
        return resp.getBody();
    }

    private List<DeviceResponse> getAll() {
        ResponseEntity<DeviceResponse[]> resp = rest.getForEntity(baseUrl(), DeviceResponse[].class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody()).isNotNull();
        return Arrays.asList(Objects.requireNonNull(resp.getBody()));
    }

    private List<DeviceResponse> getByBrand(String brand) {
        ResponseEntity<DeviceResponse[]> resp =
                rest.getForEntity(baseUrl() + "/brand/" + brand, DeviceResponse[].class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        return Arrays.asList(Objects.requireNonNull(resp.getBody()));
    }

    private List<DeviceResponse> getByState(DeviceState state) {
        ResponseEntity<DeviceResponse[]> resp =
                rest.getForEntity(baseUrl() + "/state/" + state.name(), DeviceResponse[].class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        return Arrays.asList(Objects.requireNonNull(resp.getBody()));
    }

    private DeviceResponse update(String id, String newName, String newBrand, DeviceState newState) {
        DeviceUpdateRequest req = new DeviceUpdateRequest(newName, newBrand, newState);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<DeviceUpdateRequest> entity = new HttpEntity<>(req, headers);

        ResponseEntity<DeviceResponse> resp = rest.exchange(
                baseUrl() + "/" + id,
                HttpMethod.PUT,
                entity,
                DeviceResponse.class
        );
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody()).isNotNull();
        return resp.getBody();
    }

    private ResponseEntity<Void> delete(String id) {
        return rest.exchange(baseUrl() + "/" + id, HttpMethod.DELETE, HttpEntity.EMPTY, Void.class);
    }

    // ---------- Tests for each endpoint ----------

    @Test
    void create_and_getById() {
        DeviceResponse created = create("Pixel 9", "Google", DeviceState.AVAILABLE);
        DeviceResponse fetched = getById(created.id().toString());

        assertThat(fetched.id()).isEqualTo(created.id());
        assertThat(fetched.name()).isEqualTo("Pixel 9");
        assertThat(fetched.brand()).isEqualTo("Google");
        assertThat(fetched.state()).isEqualTo(DeviceState.AVAILABLE);
        assertThat(fetched.createdAt()).isNotNull();
    }

    @Test
    void getAll_returns_seeded_and_created() {
        // There are 3 from @BeforeEach
        List<DeviceResponse> all = getAll();
        assertThat(all).hasSize(3);

        // Add one more and expect 4
        create("XPS 13", "Dell", DeviceState.AVAILABLE);
        all = getAll();
        assertThat(all).hasSize(4);

        // Sanity check that sorting by name picks the expected first
        all.sort(Comparator.comparing(DeviceResponse::name));
        assertThat(all.get(0).name()).isEqualTo("MacBook Pro"); // 'MacBook Pro' < 'ThinkPad' < 'XPS 13' < 'iPhone 15' alphabetically? (depends on sort)
    }



    @Test
    void update_modifies_fields() {
        DeviceResponse created = create("Galaxy S23", "Samsung", DeviceState.INACTIVE);

        DeviceResponse updated = update(
                created.id().toString(),
                "Galaxy S24",
                "Samsung",
                DeviceState.IN_USE
        );

        assertThat(updated.id()).isEqualTo(created.id());
        assertThat(updated.name()).isEqualTo("Galaxy S24");
        assertThat(updated.state()).isEqualTo(DeviceState.IN_USE);

        // Fetch again to confirm persisted
        DeviceResponse fetched = getById(created.id().toString());
        assertThat(fetched.name()).isEqualTo("Galaxy S24");
        assertThat(fetched.state()).isEqualTo(DeviceState.IN_USE);
    }

    @Test
    void delete_removes_resource_and_followup_get_is_404() {
        DeviceResponse created = create("Surface Pro", "Microsoft", DeviceState.AVAILABLE);

        ResponseEntity<Void> del = delete(created.id().toString());
        assertThat(del.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<DeviceResponse> afterDelete =
                rest.getForEntity(baseUrl() + "/" + created.id(), DeviceResponse.class);

        // Service should translate missing entity to 404; if it's another 4xx it will still fail this assertion.
        assertThat(afterDelete.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
