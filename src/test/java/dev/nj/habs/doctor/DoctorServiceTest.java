package dev.nj.habs.doctor;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DoctorServiceTest {

    @Mock
    DoctorRepository doctorRepository;

    @Mock
    DoctorMapper doctorMapper;

    @InjectMocks
    DoctorServiceImpl doctorService;

    @Test
    void createDoctor_validRequest_returnsDoctorResponse() {
        CreateDoctorRequest request = new CreateDoctorRequest("Lea Wong");
        Doctor savedDoctor = new Doctor("lea wong");

        when(doctorRepository.existsByDoctorName("lea wong")).thenReturn(false);
        when(doctorRepository.save(any(Doctor.class))).thenReturn(savedDoctor);
        when(doctorMapper.toResponse(any(Doctor.class))).thenReturn(new DoctorResponse(1L, "lea wong"));

        DoctorResponse response = doctorService.createDoctor(request);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("lea wong", response.doctorName());
    }
}
