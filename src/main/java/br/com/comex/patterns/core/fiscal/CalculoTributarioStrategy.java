package br.com.comex.patterns.core.fiscal;

import br.com.comex.patterns.core.model.DadosImportacao;
import br.com.comex.patterns.core.model.Regime;
import br.com.comex.patterns.core.model.TipoTributo;

import java.math.BigDecimal;

/**
 * Padrão Strategy: cada regime aduaneiro define quanto de cada tributo é efetivamente devido.
 * <p>
 * O cálculo dos valores integrais é igual para todos os regimes (feito pelo {@link SimuladorTributos});
 * o que muda de um regime para outro é a fração devida — de 0 (totalmente suspenso) a 1 (integralmente devido).
 * Para suportar um novo regime basta criar uma nova implementação, sem alterar o simulador (Open/Closed).
 */
public interface CalculoTributarioStrategy {

    Regime regime();

    /** Fração do tributo que é devida neste regime, entre 0 e 1. O restante fica suspenso. */
    BigDecimal fracaoDevida(TipoTributo tributo, DadosImportacao dados);

    /** Observação explicativa para o resultado (opcional). */
    default String observacao(DadosImportacao dados) {
        return null;
    }
}
