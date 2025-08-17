package com.example.devices.it;

import com.example.devices.domain.Device;
import com.example.devices.domain.DeviceState;
import com.example.devices.repository.DeviceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Shared JPA test logic. Subclasses only provide the vendor-specific container + properties.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ImportAutoConfiguration(exclude = FlywayAutoConfiguration.class)
public abstract class AbstractDeviceRepositoryIT {

    @Autowired
    protected DeviceRepository repository;

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
        assertThat(apple).extracting(Device::getName)
                .containsExactlyInAnyOrder("iPhone 15", "MacBook Pro");
    }

    @Test
    void findByState_works() {
        List<Device> inUse = repository.findByState(DeviceState.IN_USE);
        assertThat(inUse).hasSize(1);
        assertThat(inUse.get(0).getName()).isEqualTo("MacBook Pro");
    }
}
