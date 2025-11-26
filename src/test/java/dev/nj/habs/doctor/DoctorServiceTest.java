package dev.nj.habs.doctor;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DoctorServiceTest {

    private static final CreateDoctorRequest VALID_REQUEST = new CreateDoctorRequest("Lea Wong");

    @Mock
    DoctorRepository doctorRepository;

    @Mock
    DoctorMapper doctorMapper;

    @InjectMocks
    DoctorServiceImpl doctorService;

    @Test
    void createDoctor_validRequest_returnsDoctorResponse() {
        Doctor savedDoctor = new Doctor("lea wong");

        when(doctorRepository.existsByDoctorName("lea wong")).thenReturn(false);
        when(doctorRepository.save(any(Doctor.class))).thenReturn(savedDoctor);
        when(doctorMapper.toResponse(any(Doctor.class))).thenReturn(new DoctorResponse(1L, "lea wong"));

        DoctorResponse response = doctorService.createDoctor(VALID_REQUEST);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("lea wong", response.doctorName());
    }

    @Test
    void createDoctor_doctorAlreadyExists_throwsException() {
        when(doctorRepository.existsByDoctorName("lea wong")).thenReturn(true);

        Exception exception = assertThrows(
                DoctorAlreadyExistsException.class,
                () -> doctorService.createDoctor(VALID_REQUEST));

        assertTrue(exception.getMessage().contains("Doctor already exists"));
        verify(doctorRepository, never()).save(any(Doctor.class));
    }

    @Test
    void getAllDoctors_returnsListOfDoctors() {
        Doctor doctor1 = new Doctor( "lea wong");
        Doctor doctor2 = new Doctor( "pamella upperson");

        when(doctorRepository.findAll()).thenReturn(List.of(doctor1, doctor2));
        when(doctorMapper.toResponse(doctor1)).thenReturn(
                new DoctorResponse(1L, "lea wong"));
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
}
