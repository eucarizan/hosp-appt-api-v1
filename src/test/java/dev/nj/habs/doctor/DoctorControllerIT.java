package dev.nj.habs.doctor;

import dev.nj.habs.appointment.Appointment;
import dev.nj.habs.appointment.AppointmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;

import static dev.nj.habs.TestUtils.asJsonString;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class DoctorControllerIT {

    private static final String CREATE_DOCTOR = "/newDoctor";
    private static final String LIST_ALL_DOCTORS = "/allDoctorslist";
    private static final String LIST_AVAILABLE_DATES = "/availableDatesByDoctor";
    private static final String DELETE_DOCTOR = "/deleteDoctor";
    private static final CreateDoctorRequest VALID_REQUEST = new CreateDoctorRequest("Lea Wong");
    private static final String DR_WONG = "lea wong";

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
    MockMvc mockMvc;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @BeforeEach
    void setUp() {
        appointmentRepository.deleteAll();
        doctorRepository.deleteAll();
    }

    @Test
    void it_createDoctor_validRequest_savesAndReturnsDoctor() throws Exception {
        mockMvc.perform(post(CREATE_DOCTOR)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(VALID_REQUEST)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.doctorName").value(DR_WONG));

        assertEquals(1, doctorRepository.count());
        assertTrue(doctorRepository.existsByDoctorName(DR_WONG));
    }

    @Test
    void it_createDoctor_nullDoctor_returnsBadRequest() throws Exception {
        CreateDoctorRequest request = new CreateDoctorRequest(null);

        mockMvc.perform(post(CREATE_DOCTOR)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isBadRequest());

        assertEquals(0, doctorRepository.count());
    }

    @Test
    void it_createDoctor_emptyDoctor_returnsBadRequest() throws Exception {
        CreateDoctorRequest request = new CreateDoctorRequest("   ");

        mockMvc.perform(post(CREATE_DOCTOR)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isBadRequest());

        assertEquals(0, doctorRepository.count());
    }

    @Test
    void it_createDoctor_doctorAlreadyExists_returnsBadRequest() throws Exception {
        Doctor existingDoctor = new Doctor(DR_WONG);
        doctorRepository.save(existingDoctor);

        mockMvc.perform(post(CREATE_DOCTOR)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(VALID_REQUEST)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Doctor already exists"));

        assertEquals(1, doctorRepository.count());
    }

    @Test
    void it_getAllDoctors_withDoctors_returnsOk() throws Exception {
        Doctor doctor1 = new Doctor(DR_WONG);
        Doctor doctor2 = new Doctor("pamela upperson");
        doctorRepository.save(doctor1);
        doctorRepository.save(doctor2);

        mockMvc.perform(get(LIST_ALL_DOCTORS))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].doctorName").value(DR_WONG))
                .andExpect(jsonPath("$[1].doctorName").value("pamela upperson"));

        assertEquals(2, doctorRepository.count());
    }

    @Test
    void it_getAllDoctors_noDoctors_returnsNoContent() throws Exception {
        mockMvc.perform(get(LIST_ALL_DOCTORS))
                .andExpect(status().isNoContent());

        assertEquals(0, doctorRepository.count());
    }

    @Test
    void it_getAvailableDates_doctorExists_returnsOk() throws Exception {
        Doctor doctor = new Doctor(DR_WONG);
        doctorRepository.save(doctor);

        LocalDate tomorrow = LocalDate.now().plusDays(1);

        mockMvc.perform(get(LIST_AVAILABLE_DATES)
                        .param("doc", DR_WONG))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(4))
                .andExpect(jsonPath("$[0].availabletime").value(tomorrow.toString()))
                .andExpect(jsonPath("$[0].booked").value(false));
    }

    @Test
    void it_getAvailableDates_doctorNotExists_returnsNoContent() throws Exception {
        mockMvc.perform(get(LIST_AVAILABLE_DATES)
                        .param("doc", "unknown doctor"))
                .andExpect(status().isNoContent());
    }

    @Test
    void it_deleteDoctor_doctorExists_returnsOk() throws Exception {
        Doctor doctor = new Doctor(DR_WONG);
        doctorRepository.save(doctor);

        LocalDate tomorrow = LocalDate.now().plusDays(1);
        Appointment appointment = new Appointment(DR_WONG, "john doe", tomorrow);
        appointmentRepository.save(appointment);

        mockMvc.perform(delete(DELETE_DOCTOR)
                        .param("doc", DR_WONG))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.doctorName").value(DR_WONG));

        assertFalse(doctorRepository.existsByDoctorName(DR_WONG));
        assertTrue(doctorRepository.existsByDoctorName("director"));
        assertTrue(appointmentRepository.existsByDoctorAndDate("director", tomorrow));
    }

    @Test
    void it_deleteDoctor_doctorDoesNotExist_returnsBadRequest() throws Exception {
        mockMvc.perform(delete(DELETE_DOCTOR)
                        .param("doc", "unknown doctor"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Doctor not found"));

        assertEquals(0, doctorRepository.count());
    }
}
