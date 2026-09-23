package br.com.comex.patterns.core.fiscal.estrategias;

import br.com.comex.patterns.core.fiscal.CalculoTributarioStrategy;
import br.com.comex.patterns.core.model.DadosImportacao;
import br.com.comex.patterns.core.model.Regime;
import br.com.comex.patterns.core.model.TipoTributo;

import java.math.BigDecimal;

/**
 * Admissão Temporária com suspensão total (ex.: bens para feiras e exposições).
 * Nada é pago; os valores suspensos servem de base para a garantia / termo de responsabilidade.
 */
public class AdmissaoTemporariaSuspensaoTotalStrategy implements CalculoTributarioStrategy {

    @Override
    public Regime regime() {
        return Regime.ADMISSAO_TEMPORARIA_SUSPENSAO_TOTAL;
    }

    @Override
    public BigDecimal fracaoDevida(TipoTributo tributo, DadosImportacao dados) {
        return BigDecimal.ZERO;
    }

    @Override
    public String observacao(DadosImportacao dados) {
        return "Tributos com exigibilidade suspensa. O total suspenso é a referência para a garantia "
                + "exigida no termo de responsabilidade.";
    }
}
