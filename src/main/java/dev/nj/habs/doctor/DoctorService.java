package dev.nj.habs.doctor;

import java.util.List;

public interface DoctorService {
    DoctorResponse createDoctor(CreateDoctorRequest request);

    List<DoctorResponse> getAllDoctors();

    List<AvailableDateResponse> getAvailableDatesByDoctor(String doctor);

    DoctorResponse deleteDoctor(String doctor);
}
