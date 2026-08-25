package github.qziul.iopet.domain.repository;

import github.qziul.iopet.domain.model.Pet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PetRepository extends JpaRepository<Pet, Long> {
    Pet findByUuid(String uuid);
}
