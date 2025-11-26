package dev.nj.habs.appointment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Testcontainers
public class AppointmentServiceIT {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
        registry.add("spring.jpa.database-platform", () -> "org.hibernate.dialect.PostgreSQLDialect");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private AppointmentRepository appointmentRepository;

    private static final LocalDate DATE1 = LocalDate.of(2021, 12, 1);
    private static final LocalDate DATE2 = LocalDate.of(2022, 11, 1);

    @BeforeEach
    void setUp() { appointmentRepository.deleteAll(); }

    @Test
    void it_createAppointment_validRequest_savesToDatabase() {
        AppointmentRequest request = new AppointmentRequest("Dr. House", "John Doe", DATE1);

        AppointmentResponse response = appointmentService.createAppointment(request);

        assertNotNull(response);
        assertNotNull(response.idApp());
        assertEquals("dr. house", response.doctor());
        assertEquals("john doe", response.patient());
        assertEquals(DATE1, response.date());

        assertEquals(1, appointmentRepository.count());
        Appointment savedAppointment = appointmentRepository.findById(response.idApp()).orElseThrow();
        assertEquals("dr. house", savedAppointment.getDoctor());
        assertEquals("john doe", savedAppointment.getPatient());
        assertEquals(DATE1, savedAppointment.getDate());
    }
}
