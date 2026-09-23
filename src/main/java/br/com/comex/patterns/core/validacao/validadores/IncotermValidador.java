package br.com.comex.patterns.core.validacao.validadores;

import br.com.comex.patterns.core.model.DadosImportacao;
import br.com.comex.patterns.core.model.Incoterm;
import br.com.comex.patterns.core.validacao.ContextoValidacao;
import br.com.comex.patterns.core.validacao.ValidadorImportacao;

import java.math.BigDecimal;

/**
 * Confere se frete e seguro informados são coerentes com o Incoterm,
 * evitando contar duas vezes o que já está no preço (ex.: frete informado numa compra CIF)
 * ou deixar de somar o frete numa compra FOB.
 */
public class IncotermValidador extends ValidadorImportacao {

    @Override
    protected boolean verificar(ContextoValidacao contexto) {
        DadosImportacao d = contexto.dados();
        Incoterm incoterm = d.incoterm();

        if (incoterm.isFreteIncluso() && positivo(d.frete())) {
            contexto.erro("Incoterm %s já inclui o frete internacional no valor da mercadoria; informe frete = 0."
                    .formatted(incoterm));
        }
        if (!incoterm.isFreteIncluso() && !positivo(d.frete())) {
            contexto.erro("Incoterm %s não inclui frete internacional; informe o valor do frete."
                    .formatted(incoterm));
        }
        if (incoterm.isSeguroIncluso() && positivo(d.seguro())) {
            contexto.erro("Incoterm %s já inclui o seguro no valor da mercadoria; informe seguro = 0."
                    .formatted(incoterm));
        }
        return true;
    }

    private static boolean positivo(BigDecimal v) {
        return v != null && v.signum() > 0;
    }
}
