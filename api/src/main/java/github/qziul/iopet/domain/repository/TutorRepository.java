package github.qziul.iopet.domain.repository;

import github.qziul.iopet.domain.model.Tutor;
import org.springframework.data.jpa.repository.JpaRepository;


public interface TutorRepository extends JpaRepository<Tutor, Long> {
    Tutor findByEmail(String email);
    Tutor findByUuid(String uuid);
}
