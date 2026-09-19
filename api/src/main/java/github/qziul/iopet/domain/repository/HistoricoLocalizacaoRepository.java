package github.qziul.iopet.domain.repository;

import github.qziul.iopet.domain.model.DispositivoIot;
import github.qziul.iopet.domain.model.HistoricoLocalizacao;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HistoricoLocalizacaoRepository extends JpaRepository<HistoricoLocalizacao, Long> {
    Optional<HistoricoLocalizacao> findTop1ByDispositivoIotOrderByDataDesc(DispositivoIot dispositivoIot);
    List<HistoricoLocalizacao> findByDispositivoIotOrderByDataDesc(DispositivoIot dispositivoIot, Pageable pageable);
}
