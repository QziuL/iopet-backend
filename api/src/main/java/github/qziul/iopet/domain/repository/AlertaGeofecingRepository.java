package github.qziul.iopet.domain.repository;

import github.qziul.iopet.domain.model.AlertaGeofecing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlertaGeofecingRepository extends JpaRepository<AlertaGeofecing, Long> {
}
