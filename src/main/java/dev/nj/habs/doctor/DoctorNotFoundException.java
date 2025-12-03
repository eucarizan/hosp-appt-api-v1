package dev.nj.habs.doctor;

public class DoctorNotFoundException extends RuntimeException {
    public DoctorNotFoundException(String message) {
        super(message);
    }
}
