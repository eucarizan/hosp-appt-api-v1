package dev.nj.habs.statistics;

import dev.nj.habs.appointment.AppointmentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StatisticsController.class)
public class StatisticsControllerTest {

    private static final String GET_STATISTICS_DAY = "/statisticsDay";
    private static final String GET_STATISTICS_DOC = "/statisticsDoc";

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    AppointmentService appointmentService;

    @Test
    void getStatisticsDay_withAppointments_returnsOk() throws Exception {
        Map<String, Object> stat1 = new LinkedHashMap<>();
        stat1.put("2022-10-15", 2L);
        Map<String, Object> stat2 = new LinkedHashMap<>();
        stat2.put("2022-10-16", 1L);

        when(appointmentService.getStatisticsByDay()).thenReturn(Arrays.asList(stat1, stat2));

        mockMvc.perform(get(GET_STATISTICS_DAY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]['2022-10-15']").value(2))
                .andExpect(jsonPath("$[1]['2022-10-16']").value(1));
    }

    @Test
    void getStatisticsDay_withoutAppointments_returnsNoContent() throws Exception {
        when(appointmentService.getStatisticsByDay()).thenReturn(Collections.emptyList());

        mockMvc.perform(get(GET_STATISTICS_DAY))
                .andExpect(status().isNoContent());
    }

    @Test
    void getStatisticsDoctor_withAppointments_returnsOk() throws Exception {
        Map<String, Object> stat1 = new LinkedHashMap<>();
        stat1.put("dr. house", 3L);
        Map<String, Object> stat2 = new LinkedHashMap<>();
        stat2.put("lea wong", 2L);

        when(appointmentService.getStatisticsByDoctor()).thenReturn(Arrays.asList(stat1, stat2));

        mockMvc.perform(get(GET_STATISTICS_DOC))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]['dr. house']").value(3))
                .andExpect(jsonPath("$[1]['lea wong']").value(2));
    }
}
