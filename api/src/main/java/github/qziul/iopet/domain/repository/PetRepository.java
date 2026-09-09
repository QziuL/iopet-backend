package github.qziul.iopet.domain.repository;

import github.qziul.iopet.domain.model.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {
    Optional<Pet> findByUuid(UUID uuid);
    Optional<Pet> findByNome(String nome);
}
