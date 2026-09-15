package github.qziul.iopet.domain.repository;

import github.qziul.iopet.domain.model.Pet;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {
    @EntityGraph(attributePaths = {"tutor"})
    Optional<Pet> findByUuid(UUID uuid);
    @EntityGraph(attributePaths = {"tutor"})
    Optional<Pet> findByNome(String nome);
    @EntityGraph(attributePaths = {"tutor"})
    List<Pet> findByTutorId(Long tutorId);
}
