package br.com.comex.patterns.api.dto;

import br.com.comex.patterns.core.model.Incoterm;
import br.com.comex.patterns.core.model.Regime;
import br.com.comex.patterns.core.model.StatusProcesso;
import br.com.comex.patterns.core.model.TributoCalculado;
import br.com.comex.patterns.infra.persistence.ProcessoImportacao;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public record ProcessoResponse(Long id,
                               String referencia,
                               String importador,
                               String ncm,
                               String ncmDescricao,
                               Incoterm incoterm,
                               Regime regime,
                               String moeda,
                               BigDecimal taxaCambio,
                               BigDecimal valorMercadoria,
                               StatusProcesso status,
                               Set<StatusProcesso> proximosStatusPermitidos,
                               BigDecimal valorAduaneiro,
                               List<TributoCalculado> tributos,
                               BigDecimal totalDevido,
                               BigDecimal totalSuspenso,
                               BigDecimal custoTotalDesembaraco,
                               LocalDateTime criadoEm,
                               LocalDateTime atualizadoEm) {

    public static ProcessoResponse de(ProcessoImportacao p) {
        return new ProcessoResponse(p.getId(), p.getReferencia(), p.getImportador(), p.getNcm(),
                p.getNcmDescricao(), p.getIncoterm(), p.getRegime(), p.getMoeda(), p.getTaxaCambio(),
                p.getValorMercadoria(), p.getStatus(), p.getStatus().proximosPermitidos(),
                p.getValorAduaneiro(), p.getTributos(), p.getTotalDevido(), p.getTotalSuspenso(),
                p.getCustoTotalDesembaraco(), p.getCriadoEm(), p.getAtualizadoEm());
    }
}
