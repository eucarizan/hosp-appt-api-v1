package dev.nj.habs.appointment;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AppointServiceTest {

    @Mock
    AppointmentRepository appointmentRepository;

    @InjectMocks
    AppointmentServiceImpl appointmentService;

    @Test
    void createAppointment_validRequest_returnsAppointmentResponse() {
        Appointment appointment = new Appointment("dr. house", "john doe", LocalDate.of(2021, 12, 1));
        appointment.setId(1L);
        AppointmentRequest request = new AppointmentRequest("Dr. House", "John Doe", LocalDate.of(2021, 12, 1));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);

        AppointmentResponse response = appointmentService.createAppointment(request);

        assertNotNull(response);
        assertEquals(1L, response.idApp());
        assertEquals("dr. house", response.doctor());
        assertEquals("john doe", response.patient());
        assertEquals(LocalDate.of(2021, 12, 1), response.date());
    }
}
