package br.com.comex.patterns.infra.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistoricoStatusRepository extends JpaRepository<HistoricoStatus, Long> {

    List<HistoricoStatus> findByProcessoIdOrderByDataHoraAscIdAsc(Long processoId);
}
