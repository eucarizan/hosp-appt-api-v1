package dev.nj.habs.doctor;

import dev.nj.habs.appointment.Appointment;
import dev.nj.habs.appointment.AppointmentRepository;
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
public class DoctorServiceIT {

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
    private DoctorService doctorService;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    private static final CreateDoctorRequest VALID_REQUEST = new CreateDoctorRequest("Lea Wong");
    private static final String DR_WONG = "lea wong";

    @BeforeEach
    void setUp() {
        appointmentRepository.deleteAll();
        doctorRepository.deleteAll();
    }

    @Test
    void it_createDoctor_validRequest_savesToDatabase() {
        DoctorResponse response = doctorService.createDoctor(VALID_REQUEST);

        assertNotNull(response);
        assertNotNull(response.id());
        assertEquals(DR_WONG, response.doctorName());

        assertEquals(1, doctorRepository.count());
        Doctor savedDoctor = doctorRepository.findById(response.id()).orElseThrow();
        assertEquals(DR_WONG, savedDoctor.getDoctorName());
    }

    @Test
    void it_createDoctor_alreadyExists_throwsException() {
        Doctor existingDoctor = new Doctor(DR_WONG);
        doctorRepository.save(existingDoctor);

        Exception exception = assertThrows(
                DoctorAlreadyExistsException.class,
                () -> doctorService.createDoctor(VALID_REQUEST));

        assertTrue(exception.getMessage().contains("Doctor already exists"));
        assertEquals(1, doctorRepository.count());
    }

    @Test
    void it_getAllDoctors_returnsPersistedDoctors() {
        Doctor doctor1 = new Doctor(DR_WONG);
        Doctor doctor2 = new Doctor("pamela upperson");
        doctorRepository.save(doctor1);
        doctorRepository.save(doctor2);

        List<DoctorResponse> responses = doctorService.getAllDoctors();

        assertEquals(2, responses.size());
        assertEquals(DR_WONG, responses.get(0).doctorName());
        assertEquals("pamela upperson", responses.get(1).doctorName());
    }

    @Test
    void it_getAllDoctors_emptyDatabase_returnsEmptyList() {
        List<DoctorResponse> allDoctors = doctorService.getAllDoctors();

        assertTrue(allDoctors.isEmpty());
        assertEquals(0, doctorRepository.count());
    }

    @Test
    void it_getAvailableDates_existingDoctor_returnsAvailableDates() {
        Doctor doctor = new Doctor(DR_WONG);
        doctorRepository.save(doctor);

        LocalDate tomorrow = LocalDate.now().plusDays(1);
        List<AvailableDateResponse> availableDatesByDoctor = doctorService.getAvailableDatesByDoctor(DR_WONG);

        assertEquals(4, availableDatesByDoctor.size());
        assertEquals(tomorrow, availableDatesByDoctor.get(0).availabletime());
        assertFalse(availableDatesByDoctor.get(0).booked());
        assertEquals(tomorrow.plusDays(1), availableDatesByDoctor.get(1).availabletime());
        assertFalse(availableDatesByDoctor.get(1).booked());
        assertEquals(tomorrow.plusDays(2), availableDatesByDoctor.get(2).availabletime());
        assertFalse(availableDatesByDoctor.get(2).booked());
        assertEquals(tomorrow.plusDays(3), availableDatesByDoctor.get(3).availabletime());
        assertFalse(availableDatesByDoctor.get(3).booked());
    }

    @Test
    void it_getAvailableDates_nonExistingDoctor_throwsException() {
        String unknownDoctor = "unknown doctor";

        Exception exception = assertThrows(
                DoctorNotFoundException.class,
                () -> doctorService.getAvailableDatesByDoctor(unknownDoctor));

        assertTrue(exception.getMessage().contains("Doctor not found"));
    }

    @Test
    void it_getAvailableDates_someDatesBooked_returnsCorrectBookedStatus() {
        Doctor doctor = new Doctor(DR_WONG);
        doctorRepository.save(doctor);

        LocalDate tomorrow = LocalDate.now().plusDays(1);
        Appointment bookedAppointment = new Appointment(DR_WONG, "john doe", tomorrow);
        appointmentRepository.save(bookedAppointment);

        List<AvailableDateResponse> responses = doctorService.getAvailableDatesByDoctor(DR_WONG);

        assertEquals(4, responses.size());
        assertTrue(responses.get(0).booked());
        assertFalse(responses.get(1).booked());
        assertFalse(responses.get(2).booked());
        assertFalse(responses.get(3).booked());
    }
}
