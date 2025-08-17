package com.example.devices;

import com.example.devices.domain.Device;
import com.example.devices.domain.DeviceState;
import com.example.devices.repository.DeviceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class DeviceRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("devicesdb")
            .withUsername("devices_user")
            .withPassword("devices_pass");

    @DynamicPropertySource
    static void postgresProps(DynamicPropertyRegistry r) {
        r.add("spring.datasource.url", postgres::getJdbcUrl);
        r.add("spring.datasource.username", postgres::getUsername);
        r.add("spring.datasource.password", postgres::getPassword);
        r.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        r.add("spring.flyway.enabled", () -> "false");
    }

    @Autowired
    DeviceRepository repository;

    @BeforeEach
    void seed() {
        repository.deleteAll();

        repository.save(Device.builder().name("iPhone 15").brand("Apple").state(DeviceState.AVAILABLE).build());
        repository.save(Device.builder().name("MacBook Pro").brand("Apple").state(DeviceState.IN_USE).build());
        repository.save(Device.builder().name("ThinkPad").brand("Lenovo").state(DeviceState.INACTIVE).build());
    }

    @Test
    void findByBrandIgnoreCaseContaining_works() {
        List<Device> apple = repository.findByBrandIgnoreCaseContaining("apple");
        assertThat(apple).hasSize(2);
    }

    @Test
    void findByBrand_works() {
        List<Device> apple = repository.findByBrandIgnoreCaseContaining("Apple");
        assertThat(apple).hasSize(2);
    }

    @Test
    void findAll_works() {
        List<Device> apple = repository.findAll();
        assertThat(apple).hasSize(3);
    }

    @Test
    void findByState_works() {
        List<Device> inUse = repository.findByState(DeviceState.IN_USE);
        assertThat(inUse).hasSize(1);
        assertThat(inUse.get(0).getName()).isEqualTo("MacBook Pro");
    }

    @Test
    void findByState_AVAILABLE_works() {
        List<Device> inUse = repository.findByState(DeviceState.AVAILABLE);
        assertThat(inUse).hasSize(1);
        assertThat(inUse.get(0).getName()).isEqualTo("iPhone 15");
    }
}
