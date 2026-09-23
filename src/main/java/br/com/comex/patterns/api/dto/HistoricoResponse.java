package br.com.comex.patterns.api.dto;

import br.com.comex.patterns.core.model.StatusProcesso;
import br.com.comex.patterns.infra.persistence.HistoricoStatus;

import java.time.LocalDateTime;

public record HistoricoResponse(StatusProcesso statusAnterior,
                                StatusProcesso statusNovo,
                                String observacao,
                                LocalDateTime dataHora) {

    public static HistoricoResponse de(HistoricoStatus h) {
        return new HistoricoResponse(h.getStatusAnterior(), h.getStatusNovo(), h.getObservacao(), h.getDataHora());
    }
}
