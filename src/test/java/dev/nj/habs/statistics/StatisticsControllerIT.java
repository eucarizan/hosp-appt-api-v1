package dev.nj.habs.statistics;

import dev.nj.habs.appointment.Appointment;
import dev.nj.habs.appointment.AppointmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class StatisticsControllerIT {

    private static final String GET_STATISTICS_DAY = "/statisticsDay";
    private static final String GET_STATISTICS_DOC = "/statisticsDoc";

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
    void it_getStatisticsDay_withAppointments_returnsOk() throws Exception {
        LocalDate date1 = LocalDate.of(2022, 10, 15);
        LocalDate date2 = LocalDate.of(2022, 10, 16);
        appointmentRepository.save(new Appointment("dr. house", "john doe", date1));
        appointmentRepository.save(new Appointment("lea wong", "jane doe", date1));
        appointmentRepository.save(new Appointment("dr. house", "bob smith", date2));

        mockMvc.perform(get(GET_STATISTICS_DAY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void it_getStatisticsDay_withoutAppointments_returnsNoContent() throws Exception {
        mockMvc.perform(get(GET_STATISTICS_DAY))
                .andExpect(status().isNoContent());
    }

    @Test
    void it_getStatisticsDoctor_withAppointments_returnsOk() throws Exception {
        LocalDate date1 = LocalDate.of(2022, 10, 15);
        LocalDate date2 = LocalDate.of(2022, 10, 16);
        appointmentRepository.save(new Appointment("dr. house", "john doe", date1));
        appointmentRepository.save(new Appointment("dr. house", "jane doe", date2));
        appointmentRepository.save(new Appointment("dr. house", "bob smith", date1));
        appointmentRepository.save(new Appointment("lea wong", "alice jones", date1));
        appointmentRepository.save(new Appointment("lea wong", "charlie brown", date2));

        mockMvc.perform(get(GET_STATISTICS_DOC))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void it_getStatisticsDoctor_withoutAppointments_returnsNoContent() throws Exception {
        mockMvc.perform(get(GET_STATISTICS_DOC))
                .andExpect(status().isNoContent());
    }
}
