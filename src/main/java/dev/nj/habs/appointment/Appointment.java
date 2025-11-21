package dev.nj.habs.appointment;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDate;

@Entity
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String doctor;

    private String patient;

    private LocalDate date;

    public Appointment() {}

    public Appointment(String doctor, String patient, LocalDate date) {
        this.doctor = doctor;
        this.patient = patient;
        this.date = date;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDoctor() {
        return doctor;
    }

    public String getPatient() {
        return patient;
    }

    public LocalDate getDate() {
        return date;
    }
}
