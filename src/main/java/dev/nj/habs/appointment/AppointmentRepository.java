package dev.nj.habs.appointment;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AppointmentRepository extends ListCrudRepository<Appointment, Long> {
    boolean existsByDoctorAndDate(String doctor, LocalDate date);

    List<Appointment> findByDoctor(String doctor);

    void deleteByDoctor(String doctor);
}
