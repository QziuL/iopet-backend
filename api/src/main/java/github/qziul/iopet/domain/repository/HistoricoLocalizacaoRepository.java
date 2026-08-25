package github.qziul.iopet.domain.repository;

import github.qziul.iopet.domain.model.HistoricoLocalizacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HistoricoLocalizacaoRepository extends JpaRepository<HistoricoLocalizacao, Long> {
}
