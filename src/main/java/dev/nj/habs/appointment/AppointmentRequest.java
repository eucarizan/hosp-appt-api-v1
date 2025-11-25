package dev.nj.habs.appointment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record AppointmentRequest(
        @NotBlank(message = "Doctor field is required")
        String doctor,
        @NotBlank(message = "Patient field is required")
        String patient,
        @NotNull(message = "Date field is required")
        LocalDate date
) {
}
