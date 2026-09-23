package br.com.comex.patterns.api.dto;

import br.com.comex.patterns.core.model.NcmInfo;
import br.com.comex.patterns.core.model.Regime;
import br.com.comex.patterns.core.model.ResultadoTributos;
import br.com.comex.patterns.core.model.TributoCalculado;
import br.com.comex.patterns.service.Simulacao;

import java.math.BigDecimal;
import java.util.List;

public record SimulacaoResponse(NcmInfo ncm,
                                Regime regime,
                                BigDecimal taxaCambio,
                                BigDecimal valorAduaneiro,
                                List<TributoCalculado> tributos,
                                BigDecimal totalDevido,
                                BigDecimal totalSuspenso,
                                BigDecimal despesasAduaneiras,
                                BigDecimal custoTotalDesembaraco,
                                List<String> observacoes) {

    public static SimulacaoResponse de(Simulacao s) {
        ResultadoTributos r = s.resultado();
        return new SimulacaoResponse(s.ncm(), r.getRegime(), r.getTaxaCambio(), r.getValorAduaneiro(),
                r.getTributos(), r.getTotalDevido(), r.getTotalSuspenso(), r.getDespesasAduaneiras(),
                r.getCustoTotalDesembaraco(), r.getObservacoes());
    }
}
