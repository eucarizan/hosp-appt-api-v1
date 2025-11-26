package dev.nj.habs.appointment;

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
import static org.hamcrest.Matchers.hasItem;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class AppointmentControllerIT {

    private static final String SET_APPOINTMENTS = "/setAppointment";
    private static final AppointmentRequest VALID_REQUEST = new AppointmentRequest(
            "Dr. House", "John Doe", LocalDate.of(2021, 12, 1));

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
    private AppointmentRepository appointmentRepository;

    @BeforeEach
    void setUp() {
        appointmentRepository.deleteAll();
    }

    @Test
    void it_createAppointment_validRequest_savesAndReturnsAppointment() throws Exception {
        mockMvc.perform(post(SET_APPOINTMENTS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(VALID_REQUEST)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idApp").exists())
                .andExpect(jsonPath("$.doctor").value("dr. house"))
                .andExpect(jsonPath("$.patient").value("john doe"))
                .andExpect(jsonPath("$.date").value("2021-12-01"));

        assertEquals(1, appointmentRepository.count());
    }

    @Test
    void it_createAppointment_nullDoctor_returnsBadRequest() throws Exception {
        AppointmentRequest request = new AppointmentRequest(null, VALID_REQUEST.patient(), VALID_REQUEST.date());

        mockMvc.perform(post(SET_APPOINTMENTS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isBadRequest());

        assertEquals(0, appointmentRepository.count());
    }
}
