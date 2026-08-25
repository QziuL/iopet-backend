package github.qziul.iopet.domain.repository;

import github.qziul.iopet.domain.model.DispositivoIot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DispositivoIotRepository extends JpaRepository<DispositivoIot, Long> {
}
