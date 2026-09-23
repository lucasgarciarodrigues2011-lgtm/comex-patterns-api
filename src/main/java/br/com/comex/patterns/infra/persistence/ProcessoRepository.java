package br.com.comex.patterns.infra.persistence;

import br.com.comex.patterns.core.model.StatusProcesso;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProcessoRepository extends JpaRepository<ProcessoImportacao, Long> {

    boolean existsByReferencia(String referencia);

    List<ProcessoImportacao> findByStatusOrderByCriadoEmDesc(StatusProcesso status);

    List<ProcessoImportacao> findAllByOrderByCriadoEmDesc();
}
