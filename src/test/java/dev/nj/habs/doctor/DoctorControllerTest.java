package dev.nj.habs.doctor;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static dev.nj.habs.TestUtils.asJsonString;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DoctorController.class)
public class DoctorControllerTest {

    private static final String CREATE_DOCTOR = "/newDoctor";
    private static final CreateDoctorRequest VALID_REQUEST = new CreateDoctorRequest("Lea Wong");

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DoctorService doctorService;

    @Test
    void createDoctor_validRequest_returnsOk() throws Exception {
        DoctorResponse response = new DoctorResponse(1L, "lea wong");

        when(doctorService.createDoctor(any(CreateDoctorRequest.class))).thenReturn(response);

        mockMvc.perform(post(CREATE_DOCTOR)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(VALID_REQUEST)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.doctorName").value("lea wong"));
    }

    @Test
    void createDoctor_differentDoctor_returnsCorrectDoctor() throws Exception {
        CreateDoctorRequest request = new CreateDoctorRequest("Dr. House");
        DoctorResponse response = new DoctorResponse(2L, "dr. house");

        when(doctorService.createDoctor(any(CreateDoctorRequest.class))).thenReturn(response);

        mockMvc.perform(post(CREATE_DOCTOR)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.doctorName").value("dr. house"));
    }

    @Test
    void createDoctor_nullDoctor_returnsBadRequest() throws Exception {
        CreateDoctorRequest request = new CreateDoctorRequest(null);

        mockMvc.perform(post(CREATE_DOCTOR)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages", hasItem("Doctor field is required")));
    }

    @Test
    void createDoctor_emptyDoctor_returnsBadRequest() throws Exception {
        CreateDoctorRequest request = new CreateDoctorRequest("   ");

        mockMvc.perform(post(CREATE_DOCTOR)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages", hasItem("Doctor field is required")));
    }

    @Test
    void createDoctor_doctorAlreadyExists_returnsBadRequest() throws Exception {
        when(doctorService.createDoctor(any(CreateDoctorRequest.class)))
                .thenThrow(new DoctorAlreadyExistsException("Doctor already exists"));

        mockMvc.perform(post(CREATE_DOCTOR)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(VALID_REQUEST)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Doctor already exists"));
    }
}
