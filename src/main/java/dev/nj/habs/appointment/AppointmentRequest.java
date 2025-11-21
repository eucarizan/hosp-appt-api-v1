package dev.nj.habs.appointment;

import java.time.LocalDate;

public record AppointmentRequest(
        String doctor,
        String patient,
        LocalDate date
) {
}
