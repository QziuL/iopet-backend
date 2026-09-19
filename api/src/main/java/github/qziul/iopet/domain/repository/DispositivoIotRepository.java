package github.qziul.iopet.domain.repository;

import github.qziul.iopet.domain.model.DispositivoIot;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DispositivoIotRepository extends JpaRepository<DispositivoIot, String> {
    @EntityGraph(attributePaths = {"pet"})
    List<DispositivoIot> findByPetTutorId(Long tutorId);
}
