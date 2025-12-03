package dev.nj.habs.doctor;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static dev.nj.habs.TestUtils.asJsonString;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DoctorController.class)
public class DoctorControllerTest {

    private static final String CREATE_DOCTOR = "/newDoctor";
    private static final String LIST_ALL_DOCTORS = "/allDoctorslist";
    private static final String LIST_AVAILABLE_DATES = "/availableDatesByDoctor";
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

    @Test
    void getAllDoctors_withDoctors_returnsOk() throws Exception {
        DoctorResponse response1 = new DoctorResponse(1L, "lea wong");
        DoctorResponse response2 = new DoctorResponse(2L, "pamela upperson");

        when(doctorService.getAllDoctors()).thenReturn(List.of(response1, response2));

        mockMvc.perform(get(LIST_ALL_DOCTORS))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].doctorName").value("lea wong"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].doctorName").value("pamela upperson"));
    }

    @Test
    void getAllDoctors_noDoctors_returnsNoContent() throws Exception {
        when(doctorService.getAllDoctors()).thenReturn(List.of());

        mockMvc.perform(get(LIST_ALL_DOCTORS))
                .andExpect(status().isNoContent());
    }

    @Test
    void getAvailableDates_doctorExists_returnsOk() throws Exception {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        AvailableDateResponse date1 = new AvailableDateResponse(tomorrow, false);
        AvailableDateResponse date2 = new AvailableDateResponse(tomorrow.plusDays(1), true);

        when(doctorService.getAvailableDatesByDoctor("lea wong")).thenReturn(List.of(date1, date2));

        mockMvc.perform(get(LIST_AVAILABLE_DATES)
                        .param("doctor", "lea wong"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].booked").value(false))
                .andExpect(jsonPath("$[1].booked").value(true));
    }
}
