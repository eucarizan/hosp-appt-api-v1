package dev.nj.habs.doctor;

import jakarta.validation.constraints.NotBlank;

public record CreateDoctorRequest(
        @NotBlank(message = "Doctor field is required")
        String doctorName
) {
}
