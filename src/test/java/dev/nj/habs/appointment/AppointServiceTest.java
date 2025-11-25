package dev.nj.habs.appointment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AppointServiceTest {

    @Mock
    AppointmentRepository appointmentRepository;

    @InjectMocks
    AppointmentServiceImpl appointmentService;

    private Appointment savedAppointment;

    @BeforeEach
    void setup() {
        savedAppointment = new Appointment("dr. house", "john doe", LocalDate.of(2021, 12, 1));
    }

    @Test
    void createAppointment_validRequest_returnsAppointmentResponse() {
        savedAppointment.setId(1L);
        AppointmentRequest request = new AppointmentRequest("Dr. House", "John Doe", LocalDate.of(2021, 12, 1));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(savedAppointment);

        AppointmentResponse response = appointmentService.createAppointment(request);

        assertNotNull(response);
        assertEquals(1L, response.idApp());
        assertEquals("dr. house", response.doctor());
        assertEquals("john doe", response.patient());
        assertEquals(LocalDate.of(2021, 12, 1), response.date());
    }

    @Test
    void createAppointment_nullDoctor_throwsException() {
        AppointmentRequest request = new AppointmentRequest(null, "John Doe", LocalDate.of(2021, 12, 1));

        Exception exception = assertThrows(
                IllegalArgumentException.class,
                () -> appointmentService.createAppointment(request));

        assertTrue(exception.getMessage().contains("Doctor field is required"));
    }

    @Test
    void createAppointment_emptyDoctor_throwsException() {
        AppointmentRequest request = new AppointmentRequest("   ", "John Doe", LocalDate.of(2021, 12, 1));

        Exception exception = assertThrows(
                IllegalArgumentException.class,
                () -> appointmentService.createAppointment(request));

        assertTrue(exception.getMessage().contains("Doctor field is required"));
    }

    @Test
    void createAppointment_nullPatient_throwsException() {
        AppointmentRequest request = new AppointmentRequest("Dr. House", null, LocalDate.of(2021, 12, 1));

        Exception exception = assertThrows(
                IllegalArgumentException.class,
                () -> appointmentService.createAppointment(request));

        assertTrue(exception.getMessage().contains("Patient field is required"));
    }

    @Test
    void createAppointment_emptyPatient_throwsException() {
        AppointmentRequest request = new AppointmentRequest("Dr. House", "   ", LocalDate.of(2021, 12, 1));

        Exception exception = assertThrows(
                IllegalArgumentException.class,
                () -> appointmentService.createAppointment(request));

        assertTrue(exception.getMessage().contains("Patient field is required"));
    }

    @Test
    void createAppointment_nullDate_throwsException() {
        AppointmentRequest request = new AppointmentRequest("Dr. House", "John Doe", null);

        Exception exception = assertThrows(
                IllegalArgumentException.class,
                () -> appointmentService.createAppointment(request));

        assertTrue(exception.getMessage().contains("Date field is required"));
    }

    @Test
    void deleteAppointment_existingId_returnsDeletedAppointment() {
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(savedAppointment));

        Appointment response = appointmentService.deleteAppointment(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        verify(appointmentRepository).delete(savedAppointment);
    }
}
