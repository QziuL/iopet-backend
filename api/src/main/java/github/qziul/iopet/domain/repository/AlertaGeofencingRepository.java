package github.qziul.iopet.domain.repository;

import github.qziul.iopet.domain.model.AlertaGeofencing;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AlertaGeofencingRepository extends JpaRepository<AlertaGeofencing, Long> {
    @EntityGraph(attributePaths = {"pet", "historicoLocalizacao"})
    List<AlertaGeofencing> findByPetTutorIdOrderByDataDesc(Long tutorId);

    @EntityGraph(attributePaths = {"pet", "historicoLocalizacao"})
    List<AlertaGeofencing> findByPetUuidOrderByDataDesc(UUID petUuid);
}
