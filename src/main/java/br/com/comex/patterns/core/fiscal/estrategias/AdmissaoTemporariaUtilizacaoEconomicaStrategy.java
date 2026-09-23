package br.com.comex.patterns.core.fiscal.estrategias;

import br.com.comex.patterns.core.fiscal.CalculoTributarioStrategy;
import br.com.comex.patterns.core.fiscal.ParametrosFiscais;
import br.com.comex.patterns.core.model.DadosImportacao;
import br.com.comex.patterns.core.model.Regime;
import br.com.comex.patterns.core.model.TipoTributo;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Admissão Temporária para utilização econômica: os tributos federais são pagos proporcionalmente
 * ao tempo de permanência — 1% ao mês sobre o valor integral, limitado a 100%.
 * Simplificação didática: o ICMS é tratado como suspenso (na prática depende da legislação de cada estado).
 */
public class AdmissaoTemporariaUtilizacaoEconomicaStrategy implements CalculoTributarioStrategy {

    @Override
    public Regime regime() {
        return Regime.ADMISSAO_TEMPORARIA_UTILIZACAO_ECONOMICA;
    }

    @Override
    public BigDecimal fracaoDevida(TipoTributo tributo, DadosImportacao dados) {
        if (!tributo.isFederal()) {
            return BigDecimal.ZERO;
        }
        BigDecimal percentual = ParametrosFiscais.INSTANCIA.percentualMensalUtilizacaoEconomica()
                .multiply(BigDecimal.valueOf(dados.mesesPermanencia()));
        BigDecimal fracao = percentual.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
        return fracao.min(BigDecimal.ONE);
    }

    @Override
    public String observacao(DadosImportacao dados) {
        return "Tributos federais devidos proporcionalmente: 1%% ao mês x %d meses de permanência."
                .formatted(dados.mesesPermanencia());
    }
}
