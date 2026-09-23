package br.com.comex.patterns.core.fiscal.estrategias;

import br.com.comex.patterns.core.fiscal.CalculoTributarioStrategy;
import br.com.comex.patterns.core.model.DadosImportacao;
import br.com.comex.patterns.core.model.Regime;
import br.com.comex.patterns.core.model.TipoTributo;

import java.math.BigDecimal;

/**
 * Drawback suspensão: II, IPI, PIS e COFINS ficam suspensos, condicionados à exportação do produto final.
 * Simplificação didática: o ICMS é tratado como devido (o tratamento real varia por estado).
 */
public class DrawbackSuspensaoStrategy implements CalculoTributarioStrategy {

    @Override
    public Regime regime() {
        return Regime.DRAWBACK_SUSPENSAO;
    }

    @Override
    public BigDecimal fracaoDevida(TipoTributo tributo, DadosImportacao dados) {
        return tributo.isFederal() ? BigDecimal.ZERO : BigDecimal.ONE;
    }

    @Override
    public String observacao(DadosImportacao dados) {
        return "Tributos federais suspensos, condicionados à exportação do produto final no prazo do ato concessório.";
    }
}
