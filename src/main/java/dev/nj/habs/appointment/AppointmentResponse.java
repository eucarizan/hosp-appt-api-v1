package dev.nj.habs.appointment;

import java.time.LocalDate;

public record AppointmentResponse(
        Long idApp,
        String doctor,
        String patient,
        LocalDate date
) {
}
