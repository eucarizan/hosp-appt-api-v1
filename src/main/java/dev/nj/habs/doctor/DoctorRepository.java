package dev.nj.habs.doctor;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DoctorRepository extends CrudRepository<Doctor, Long> {
    boolean existsByDoctorName(String doctorName);
}
