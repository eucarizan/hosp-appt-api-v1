package dev.nj.habs.appointment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AppointServiceTest {

    @Mock
    AppointmentRepository appointmentRepository;

    @Mock
    AppointmentMapper appointmentMapper;

    @InjectMocks
    AppointmentServiceImpl appointmentService;

    private Appointment savedAppointment;
    private AppointmentResponse appointmentResponse;
    private static final LocalDate DATE1 = LocalDate.of(2021, 12, 1);
    private static final LocalDate DATE2 = LocalDate.of(2022, 11, 1);

    @BeforeEach
    void setup() {
        savedAppointment = new Appointment("dr. house", "john doe", DATE1);
        appointmentResponse = new AppointmentResponse(1L, "dr. house", "john doe", DATE1);
    }

    @Test
    void createAppointment_validRequest_returnsAppointmentResponse() {
        AppointmentRequest request = new AppointmentRequest("Dr. House", "John Doe", DATE1);
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(savedAppointment);
        when(appointmentMapper.toResponse(any(Appointment.class))).thenReturn(appointmentResponse);

        AppointmentResponse response = appointmentService.createAppointment(request);

        assertNotNull(response);
        assertEquals(1L, response.idApp());
        assertEquals("dr. house", response.doctor());
        assertEquals("john doe", response.patient());
        assertEquals(DATE1, response.date());
    }

    @Test
    void createAppointment_nullDoctor_throwsException() {
        AppointmentRequest request = new AppointmentRequest(null, "John Doe", DATE1);

        Exception exception = assertThrows(
                IllegalArgumentException.class,
                () -> appointmentService.createAppointment(request));

        assertTrue(exception.getMessage().contains("Doctor field is required"));
    }

    @Test
    void createAppointment_emptyDoctor_throwsException() {
        AppointmentRequest request = new AppointmentRequest("   ", "John Doe", DATE1);

        Exception exception = assertThrows(
                IllegalArgumentException.class,
                () -> appointmentService.createAppointment(request));

        assertTrue(exception.getMessage().contains("Doctor field is required"));
    }

    @Test
    void createAppointment_nullPatient_throwsException() {
        AppointmentRequest request = new AppointmentRequest("Dr. House", null, DATE1);

        Exception exception = assertThrows(
                IllegalArgumentException.class,
                () -> appointmentService.createAppointment(request));

        assertTrue(exception.getMessage().contains("Patient field is required"));
    }

    @Test
    void createAppointment_emptyPatient_throwsException() {
        AppointmentRequest request = new AppointmentRequest("Dr. House", "   ", DATE1);

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
        when(appointmentMapper.toResponse(any(Appointment.class))).thenReturn(appointmentResponse);

        AppointmentResponse response = appointmentService.deleteAppointment(1L);

        assertNotNull(response);
        assertEquals(1L, response.idApp());
        verify(appointmentRepository).delete(savedAppointment);
    }

    @Test
    void deleteAppointment_nonExistingId_throwsAppointmentNotFoundException() {
        Long nonExistingId = 999L;
        when(appointmentRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(
                AppointmentNotFoundException.class,
                () -> appointmentService.deleteAppointment(nonExistingId));

        assertTrue(exception.getMessage().contains("The appointment does not exist or was already cancelled"));
    }

    @Test
    void getAllAppointments_returnsListOfAppointments() {
        Appointment appointment2 = new Appointment("lea wong", "jane doe", DATE2);

        when(appointmentRepository.findAll()).thenReturn(List.of(savedAppointment, appointment2));
        when(appointmentMapper.toResponse(savedAppointment)).thenReturn(
                new AppointmentResponse(1L, "dr. house", "john doe", DATE1));
        when(appointmentMapper.toResponse(appointment2)).thenReturn(
                new AppointmentResponse(2L, "lea wong", "jane doe", DATE2));

        List<AppointmentResponse> responses = appointmentService.getAllAppointments();

        assertEquals(2, responses.size());
        assertEquals(1L, responses.get(0).idApp());
        assertEquals(2L, responses.get(1).idApp());
    }
}
