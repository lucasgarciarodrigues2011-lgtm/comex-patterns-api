package br.com.comex.patterns.core.validacao.validadores;

import br.com.comex.patterns.core.model.DadosImportacao;
import br.com.comex.patterns.core.model.Regime;
import br.com.comex.patterns.core.validacao.ContextoValidacao;
import br.com.comex.patterns.core.validacao.ValidadorImportacao;

/** Regras específicas de cada regime aduaneiro. */
public class RegimeValidador extends ValidadorImportacao {

    @Override
    protected boolean verificar(ContextoValidacao contexto) {
        DadosImportacao d = contexto.dados();
        boolean admissaoTemporaria = d.regime() == Regime.ADMISSAO_TEMPORARIA_SUSPENSAO_TOTAL
                || d.regime() == Regime.ADMISSAO_TEMPORARIA_UTILIZACAO_ECONOMICA;

        if (admissaoTemporaria && (d.mesesPermanencia() == null || d.mesesPermanencia() <= 0)) {
            contexto.erro("Admissão Temporária exige o prazo de permanência em meses (maior que zero).");
        }
        if (!admissaoTemporaria && d.mesesPermanencia() != null) {
            contexto.erro("Prazo de permanência só se aplica a regimes de Admissão Temporária.");
        }
        return true;
    }
}
