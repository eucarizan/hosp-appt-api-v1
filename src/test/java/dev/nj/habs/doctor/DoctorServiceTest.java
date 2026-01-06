package dev.nj.habs.doctor;

import dev.nj.habs.appointment.AppointmentRepository;
import dev.nj.habs.appointment.AppointmentService;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DoctorServiceTest {

    private static final CreateDoctorRequest VALID_REQUEST = new CreateDoctorRequest("Lea Wong");
    private static final String DR_WONG = "lea wong";

    @Mock
    DoctorRepository doctorRepository;

    @Mock
    AppointmentRepository appointmentRepository;

    @Mock
    DoctorMapper doctorMapper;

    @Mock
    AppointmentService appointmentService;;

    @InjectMocks
    DoctorServiceImpl doctorService;

    @Test
    void createDoctor_validRequest_returnsDoctorResponse() {
        Doctor savedDoctor = new Doctor(DR_WONG);

        when(doctorRepository.existsByDoctorName(DR_WONG)).thenReturn(false);
        when(doctorRepository.save(any(Doctor.class))).thenReturn(savedDoctor);
        when(doctorMapper.toResponse(any(Doctor.class))).thenReturn(new DoctorResponse(1L, DR_WONG));

        DoctorResponse response = doctorService.createDoctor(VALID_REQUEST);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("lea wong", response.doctorName());
    }

    @Test
    void createDoctor_doctorAlreadyExists_throwsException() {
        when(doctorRepository.existsByDoctorName(DR_WONG)).thenReturn(true);

        Exception exception = assertThrows(
                DoctorAlreadyExistsException.class,
                () -> doctorService.createDoctor(VALID_REQUEST));

        assertTrue(exception.getMessage().contains("Doctor already exists"));
        verify(doctorRepository, never()).save(any(Doctor.class));
    }

    @Test
    void getAllDoctors_returnsListOfDoctors() {
        Doctor doctor1 = new Doctor(DR_WONG);
        Doctor doctor2 = new Doctor("pamella upperson");

        when(doctorRepository.findAll()).thenReturn(List.of(doctor1, doctor2));
        when(doctorMapper.toResponse(doctor1)).thenReturn(
                new DoctorResponse(1L, DR_WONG));
        when(doctorMapper.toResponse(doctor2)).thenReturn(
                new DoctorResponse(2L, "pamella upperson"));

        List<DoctorResponse> responses = doctorService.getAllDoctors();

        assertEquals(2, responses.size());
        assertEquals(1L, responses.get(0).id());
        assertEquals("lea wong", responses.get(0).doctorName());
        assertEquals(2L, responses.get(1).id());
        assertEquals("pamella upperson", responses.get(1).doctorName());
    }

    @Test
    void getAllDoctors_emptyList_returnsEmptyList() {
        when(doctorRepository.findAll()).thenReturn(List.of());

        List<DoctorResponse> responses = doctorService.getAllDoctors();

        assertTrue(responses.isEmpty());
    }

    @Test
    void getAvailableDates_existsDoctor_returnsFourAvailableDates() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);

        when(doctorRepository.existsByDoctorName(DR_WONG)).thenReturn(true);
        when(appointmentRepository.existsByDoctorAndDate(DR_WONG, tomorrow)).thenReturn(false);
        when(appointmentRepository.existsByDoctorAndDate(DR_WONG, tomorrow.plusDays(1))).thenReturn(false);
        when(appointmentRepository.existsByDoctorAndDate(DR_WONG, tomorrow.plusDays(2))).thenReturn(false);
        when(appointmentRepository.existsByDoctorAndDate(DR_WONG, tomorrow.plusDays(3))).thenReturn(false);

        List<AvailableDateResponse> responses = doctorService.getAvailableDatesByDoctor(DR_WONG);

        assertEquals(4, responses.size());
        assertEquals(tomorrow, responses.get(0).availabletime());
        assertFalse(responses.get(0).booked());
        assertEquals(tomorrow.plusDays(1), responses.get(1).availabletime());
        assertFalse(responses.get(1).booked());
        assertEquals(tomorrow.plusDays(2), responses.get(2).availabletime());
        assertFalse(responses.get(2).booked());
        assertEquals(tomorrow.plusDays(3), responses.get(3).availabletime());
        assertFalse(responses.get(3).booked());
    }

    @Test
    void getAvailableDates_nonExistingDoctor_returnsEmptyList() {
        String doctorName = "unknown doctor";

        when(doctorRepository.existsByDoctorName(doctorName)).thenReturn(false);

        Exception exception = assertThrows(
                DoctorNotFoundException.class,
                () -> doctorService.getAvailableDatesByDoctor(doctorName));

        assertTrue(exception.getMessage().contains("Doctor not found"));
    }

    @Test
    void getAvailableDates_someDatesBooked_returnsCorrectBookedStatus() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);

        when(doctorRepository.existsByDoctorName(DR_WONG)).thenReturn(true);
        when(appointmentRepository.existsByDoctorAndDate(DR_WONG, tomorrow)).thenReturn(true);
        when(appointmentRepository.existsByDoctorAndDate(DR_WONG, tomorrow.plusDays(1))).thenReturn(false);
        when(appointmentRepository.existsByDoctorAndDate(DR_WONG, tomorrow.plusDays(2))).thenReturn(false);
        when(appointmentRepository.existsByDoctorAndDate(DR_WONG, tomorrow.plusDays(3))).thenReturn(false);

        List<AvailableDateResponse> responses = doctorService.getAvailableDatesByDoctor(DR_WONG);

        assertEquals(4, responses.size());
        assertTrue(responses.get(0).booked());
        assertFalse(responses.get(1).booked());
        assertFalse(responses.get(2).booked());
        assertFalse(responses.get(3).booked());
    }

    @Test
    void deleteDoctor_regularDoctor_transfersAppointmentsToDirector() {
        Doctor savedDoctor = new Doctor("lea wong");
        savedDoctor.setId(1L);
        when(doctorRepository.findByDoctorName("lea wong")).thenReturn(Optional.of(savedDoctor));
        when(doctorRepository.existsByDoctorName("director")).thenReturn(true);
        when(doctorMapper.toResponse(savedDoctor)).thenReturn(new DoctorResponse(1L, DR_WONG));

        DoctorResponse response = doctorService.deleteDoctor("lea wong");

        assertNotNull(response);
        assertEquals("lea wong", response.doctorName());
        verify(appointmentService).transferAppointmentsToDirector("lea wong");
        verify(doctorRepository).delete(savedDoctor);
    }
}
