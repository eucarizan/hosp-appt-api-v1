package dev.nj.habs.doctor;

import java.time.LocalDate;

public record AvailableDateResponse(
        LocalDate availabletime,
        boolean booked
) {
}
