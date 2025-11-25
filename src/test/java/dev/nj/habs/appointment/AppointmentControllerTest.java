package dev.nj.habs.appointment;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static dev.nj.habs.TestUtils.asJsonString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AppointmentController.class)
public class AppointmentControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    AppointmentService appointmentService;

    @Test
    void createAppointment_validRequest_returnsOk() throws Exception {
        AppointmentRequest request = new AppointmentRequest("Dr. House", "John Doe", LocalDate.of(2021, 12, 1));
        AppointmentResponse response = new AppointmentResponse(1L, "dr. house", "john doe", LocalDate.of(2021, 12, 1));

        when(appointmentService.createAppointment(any(AppointmentRequest.class))).thenReturn(response);

        mockMvc.perform(post("/setAppointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idApp").value(1))
                .andExpect(jsonPath("$.doctor").value("dr. house"))
                .andExpect(jsonPath("$.patient").value("john doe"));
    }
}
