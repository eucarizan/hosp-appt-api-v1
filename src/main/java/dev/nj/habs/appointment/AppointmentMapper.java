package dev.nj.habs.appointment;

import org.springframework.stereotype.Component;

@Component
public class AppointmentMapper {
    public AppointmentResponse toResponse(Appointment appointment) {
        return new AppointmentResponse(
                appointment.getId(),
                appointment.getDoctor(),
                appointment.getPatient(),
                appointment.getDate()
        );
    }
}
