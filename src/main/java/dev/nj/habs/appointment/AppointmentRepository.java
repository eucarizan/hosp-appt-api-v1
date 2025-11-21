package dev.nj.habs.appointment;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppointmentRepository extends ListCrudRepository<Appointment, Long> {
}
