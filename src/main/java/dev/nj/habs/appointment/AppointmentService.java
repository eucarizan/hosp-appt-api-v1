package dev.nj.habs.appointment;

import java.util.List;

public interface AppointmentService {
    AppointmentResponse createAppointment(AppointmentRequest request);

    AppointmentResponse deleteAppointment(Long id);

    List<AppointmentResponse> getAllAppointments();

    void transferAppointmentsToDirector(String doctorName);

    void deleteAppointmentsByDoctor(String doctorName);
}
