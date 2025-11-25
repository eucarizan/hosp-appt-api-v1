package dev.nj.habs.appointment;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

import static dev.nj.habs.TestUtils.asJsonString;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AppointmentController.class)
public class AppointmentControllerTest {

    private static final String SET_APPOINTMENTS = "/setAppointments";
    private static final String DELETE_APPOINTMENTS = "/deleteAppointment";
    private static final String GET_APPOINTMENTS = "/appointments";
    private static final AppointmentRequest VALID_REQUEST = new AppointmentRequest(
            "Dr. House", "John Doe", LocalDate.of(2021, 12, 1)
    );
    private static final AppointmentResponse VALID_RESPONSE = new AppointmentResponse(
            1L, "dr. house", "john doe", LocalDate.of(2021, 12, 1)
    );

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    AppointmentService appointmentService;

    @Test
    void createAppointment_validRequest_returnsOk() throws Exception {
        when(appointmentService.createAppointment(any(AppointmentRequest.class))).thenReturn(VALID_RESPONSE);

        mockMvc.perform(post(SET_APPOINTMENTS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(VALID_REQUEST)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idApp").value(1))
                .andExpect(jsonPath("$.doctor").value("dr. house"))
                .andExpect(jsonPath("$.patient").value("john doe"));
    }

    @Test
    void createAppointment_nullDoctor_returnsBadRequest() throws Exception {
        AppointmentRequest request = new AppointmentRequest(null, VALID_REQUEST.patient(), VALID_REQUEST.date());

        mockMvc.perform(post(SET_APPOINTMENTS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages").isArray())
                .andExpect(jsonPath("$.messages", hasItem("Doctor field is required")));
    }

    @Test
    void createAppointment_emptyDoctor_returnsBadRequest() throws Exception {
        AppointmentRequest request = new AppointmentRequest("   ", VALID_REQUEST.patient(), VALID_REQUEST.date());

        mockMvc.perform(post(SET_APPOINTMENTS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages").isArray())
                .andExpect(jsonPath("$.messages", hasItem("Doctor field is required")));
    }

    @Test
    void createAppointment_nullPatient_returnsBadRequest() throws Exception {
        AppointmentRequest request = new AppointmentRequest(VALID_REQUEST.doctor(), null, VALID_REQUEST.date());

        mockMvc.perform(post(SET_APPOINTMENTS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages").isArray())
                .andExpect(jsonPath("$.messages", hasItem("Patient field is required")));
    }

    @Test
    void createAppointment_emptyPatient_returnsBadRequest() throws Exception {
        AppointmentRequest request = new AppointmentRequest(VALID_REQUEST.doctor(), "   ", VALID_REQUEST.date());

        mockMvc.perform(post(SET_APPOINTMENTS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages").isArray())
                .andExpect(jsonPath("$.messages", hasItem("Patient field is required")));
    }

    @Test
    void createAppointment_nullDate_returnsBadRequest() throws Exception {
        AppointmentRequest request = new AppointmentRequest(VALID_REQUEST.doctor(), VALID_REQUEST.patient(), null);

        mockMvc.perform(post(SET_APPOINTMENTS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages").isArray())
                .andExpect(jsonPath("$.messages", hasItem("Date field is required")));
    }

    @Test
    void deleteAppointment_existingId_returnsOk() throws Exception {
        when(appointmentService.deleteAppointment(1L)).thenReturn(VALID_RESPONSE);

        mockMvc.perform(delete(DELETE_APPOINTMENTS)
                        .param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idApp").value(1))
                .andExpect(jsonPath("$.doctor").value("dr. house"));
    }

    @Test
    void deleteAppointment_nonExistingId_returnsBadRequest() throws Exception {
        when(appointmentService.deleteAppointment(999L))
                .thenThrow(new AppointmentNotFoundException("The appointment does not exist or was already cancelled"));

        mockMvc.perform(delete(DELETE_APPOINTMENTS)
                        .param("id", "999"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("The appointment does not exist or was already cancelled"));
    }

    @Test
    void getAllAppointments_withAppointments_returnsOk() throws Exception {
        AppointmentResponse response2 = new AppointmentResponse(2L, "lea wong", "jane doe", LocalDate.of(2022, 11, 1));

        when(appointmentService.getAllAppointments()).thenReturn(List.of(VALID_RESPONSE, response2));

        mockMvc.perform(get(GET_APPOINTMENTS))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idApp").value(1))
                .andExpect(jsonPath("$[1].idApp").value(2));
    }
}
