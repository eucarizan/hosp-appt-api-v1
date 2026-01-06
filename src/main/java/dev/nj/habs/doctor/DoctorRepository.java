package dev.nj.habs.doctor;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DoctorRepository extends ListCrudRepository<Doctor, Long> {
    boolean existsByDoctorName(String doctorName);

    Optional<Doctor> findByDoctorName(String doctorName);
}
