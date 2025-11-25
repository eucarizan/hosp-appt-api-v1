package dev.nj.habs.appointment;

public interface AppointmentService {
    AppointmentResponse createAppointment(AppointmentRequest request);

    AppointmentResponse deleteAppointment(Long id);
}
