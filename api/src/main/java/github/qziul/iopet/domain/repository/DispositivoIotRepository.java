package github.qziul.iopet.domain.repository;

import github.qziul.iopet.domain.model.DispositivoIot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DispositivoIotRepository extends JpaRepository<DispositivoIot, String> {
    Optional<DispositivoIot> findByEnderecoMac(String enderecoMac);
}
