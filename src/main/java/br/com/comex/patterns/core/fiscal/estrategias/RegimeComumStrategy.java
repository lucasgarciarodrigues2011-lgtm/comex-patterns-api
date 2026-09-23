package br.com.comex.patterns.core.fiscal.estrategias;

import br.com.comex.patterns.core.fiscal.CalculoTributarioStrategy;
import br.com.comex.patterns.core.model.DadosImportacao;
import br.com.comex.patterns.core.model.Regime;
import br.com.comex.patterns.core.model.TipoTributo;

import java.math.BigDecimal;

/** Importação comum: todos os tributos são integralmente devidos. */
public class RegimeComumStrategy implements CalculoTributarioStrategy {

    @Override
    public Regime regime() {
        return Regime.COMUM;
    }

    @Override
    public BigDecimal fracaoDevida(TipoTributo tributo, DadosImportacao dados) {
        return BigDecimal.ONE;
    }
}
