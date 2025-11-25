package dev.nj.habs.appointment;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static dev.nj.habs.TestUtils.asJsonString;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AppointmentController.class)
public class AppointmentControllerTest {

    private static final String SET_APPOINTMENTS = "/setAppointments";
    private static final AppointmentRequest VALID_REQUEST = new AppointmentRequest(
            "Dr. House", "John Doe", LocalDate.of(2021, 12, 1)
    );

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    AppointmentService appointmentService;

    @Test
    void createAppointment_validRequest_returnsOk() throws Exception {
        AppointmentRequest request = new AppointmentRequest(VALID_REQUEST.doctor(), VALID_REQUEST.patient(), VALID_REQUEST.date());
        AppointmentResponse response = new AppointmentResponse(1L, "dr. house", "john doe", LocalDate.of(2021, 12, 1));

        when(appointmentService.createAppointment(any(AppointmentRequest.class))).thenReturn(response);

        mockMvc.perform(post(SET_APPOINTMENTS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idApp").value(1))
                .andExpect(jsonPath("$.doctor").value("dr. house"))
                .andExpect(jsonPath("$.patient").value("john doe"));
    }

    @Test
    void createAppointment_nullDoctor_returns400() throws Exception {
        AppointmentRequest request = new AppointmentRequest(null, VALID_REQUEST.patient(), VALID_REQUEST.date());

        mockMvc.perform(post(SET_APPOINTMENTS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages").isArray())
                .andExpect(jsonPath("$.messages", hasItem("Doctor field is required")));
    }

    @Test
    void createAppointment_emptyDoctor_returns400() throws Exception {
        AppointmentRequest request = new AppointmentRequest("   ", VALID_REQUEST.patient(), VALID_REQUEST.date());

        mockMvc.perform(post(SET_APPOINTMENTS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages").isArray())
                .andExpect(jsonPath("$.messages", hasItem("Doctor field is required")));
    }

    @Test
    void createAppointment_nullPatient_returns400() throws Exception {
        AppointmentRequest request = new AppointmentRequest(VALID_REQUEST.doctor(), null, VALID_REQUEST.date());

        mockMvc.perform(post(SET_APPOINTMENTS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages").isArray())
                .andExpect(jsonPath("$.messages", hasItem("Patient field is required")));
    }

    @Test
    void createAppointment_emptyPatient_returns400() throws Exception {
        AppointmentRequest request = new AppointmentRequest(VALID_REQUEST.doctor(), "   ", VALID_REQUEST.date());

        mockMvc.perform(post(SET_APPOINTMENTS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages").isArray())
                .andExpect(jsonPath("$.messages", hasItem("Patient field is required")));
    }

    @Test
    void createAppointment_nullDate_returns400() throws Exception {
        AppointmentRequest request = new AppointmentRequest(VALID_REQUEST.doctor(), VALID_REQUEST.patient(), null);

        mockMvc.perform(post(SET_APPOINTMENTS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages").isArray())
                .andExpect(jsonPath("$.messages", hasItem("Date field is required")));
    }
}
