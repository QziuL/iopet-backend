package github.qziul.iopet.domain.repository;

import github.qziul.iopet.domain.model.Tutor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TutorRepository extends JpaRepository<Tutor, Long> {
    Optional<Tutor> findByEmail(String email);
    Optional<Tutor> findByUuid(UUID uuid);
}
