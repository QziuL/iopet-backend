package github.qziul.iopet.domain.repository;

import github.qziul.iopet.domain.model.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {
    Optional<Pet> findByUuid(UUID uuid);
    Optional<Pet> findByNome(String nome);
    List<Pet> findByTutorId(Long tutorId);
}
