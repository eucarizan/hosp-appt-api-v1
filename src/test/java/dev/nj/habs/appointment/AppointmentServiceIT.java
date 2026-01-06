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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    void it_createAppointment_doctorIsDirector_throwsException() {
        AppointmentRequest request = new AppointmentRequest("director", "John Doe", DATE1);

        Exception exception = assertThrows(
                IllegalArgumentException.class,
                () -> appointmentService.createAppointment(request));

        assertEquals("Cannot set appointments for director", exception.getMessage());
        assertEquals(0, appointmentRepository.count());
    }

    @Test
    void it_deleteAppointment_existingId_removesFromDatabase() {
        Appointment appointment = new Appointment("dr. house", "john doe", DATE1);
        appointment = appointmentRepository.save(appointment);
        Long appointmentId = appointment.getId();

        AppointmentResponse response = appointmentService.deleteAppointment(appointmentId);

        assertNotNull(response);
        assertEquals(appointmentId, response.idApp());
        assertEquals("dr. house", response.doctor());
        assertEquals("john doe", response.patient());
        assertEquals(DATE1, response.date());

        assertEquals(0, appointmentRepository.count());
        assertTrue(appointmentRepository.findById(appointmentId).isEmpty());
    }

    @Test
    void it_deleteAppointment_nonExistingId_throwsException() {
        Long nonExistingId = 999L;

        Exception exception = assertThrows(
                AppointmentNotFoundException.class,
                () -> appointmentService.deleteAppointment(nonExistingId));

        assertTrue(exception.getMessage().contains("The appointment does not exist or was already cancelled"));
    }

    @Test
    void it_getAppointments_returnsPersistedAppointments() {
        Appointment appointment1 = new Appointment("dr. house", "john doe", DATE1);
        Appointment appointment2 = new Appointment("lea wong", "jane doe", DATE2);
        appointmentRepository.save(appointment1);
        appointmentRepository.save(appointment2);

        List<AppointmentResponse> responses = appointmentService.getAllAppointments();

        assertEquals(2, responses.size());
        assertEquals("dr. house", responses.get(0).doctor());
        assertEquals("john doe", responses.get(0).patient());
        assertEquals("lea wong", responses.get(1).doctor());
        assertEquals("jane doe", responses.get(1).patient());
    }

    @Test
    void it_getAppointments_emptyDatabase_returnsEmptyList() {
        List<AppointmentResponse> responses = appointmentService.getAllAppointments();

        assertTrue(responses.isEmpty());
        assertEquals(0, appointmentRepository.count());
    }
}
