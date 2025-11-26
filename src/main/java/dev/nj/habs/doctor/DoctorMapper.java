package dev.nj.habs.doctor;

import org.springframework.stereotype.Component;

@Component
public class DoctorMapper {
    public DoctorResponse toResponse(Doctor doctor) {
        return new DoctorResponse(
                doctor.getId(),
                doctor.getDoctorName()
        );
    }
}
