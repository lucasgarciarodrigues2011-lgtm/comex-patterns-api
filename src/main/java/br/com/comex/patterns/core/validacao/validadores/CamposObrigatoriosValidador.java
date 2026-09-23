package br.com.comex.patterns.core.validacao.validadores;

import br.com.comex.patterns.core.model.DadosImportacao;
import br.com.comex.patterns.core.validacao.ContextoValidacao;
import br.com.comex.patterns.core.validacao.ValidadorImportacao;

import java.math.BigDecimal;

/** Primeiro elo: sem os dados básicos, nenhuma outra regra pode ser avaliada. */
public class CamposObrigatoriosValidador extends ValidadorImportacao {

    @Override
    protected boolean verificar(ContextoValidacao contexto) {
        DadosImportacao d = contexto.dados();
        int errosAntes = contexto.erros().size();

        if (d.ncm() == null || !d.ncm().replace(".", "").matches("\\d{8}")) {
            contexto.erro("NCM deve ter 8 dígitos (ex.: 8474.20.10 ou 84742010).");
        }
        if (d.incoterm() == null) {
            contexto.erro("Incoterm é obrigatório.");
        }
        if (d.regime() == null) {
            contexto.erro("Regime aduaneiro é obrigatório.");
        }
        if (d.moeda() == null || !d.moeda().matches("[A-Z]{3}")) {
            contexto.erro("Moeda deve ser um código ISO de 3 letras maiúsculas (ex.: USD).");
        }
        if (!positivo(d.valorMercadoria())) {
            contexto.erro("Valor da mercadoria deve ser maior que zero.");
        }
        if (!positivo(d.taxaCambio())) {
            contexto.erro("Taxa de câmbio deve ser maior que zero.");
        }
        if (negativo(d.frete()) || negativo(d.seguro()) || negativo(d.despesasAduaneiras())) {
            contexto.erro("Frete, seguro e despesas não podem ser negativos.");
        }
        if (d.aliquotaIcms() == null || negativo(d.aliquotaIcms())
                || d.aliquotaIcms().compareTo(BigDecimal.valueOf(100)) >= 0) {
            contexto.erro("Alíquota de ICMS deve estar entre 0 e 100 (exclusivo).");
        }
        return contexto.erros().size() == errosAntes;
    }

    private static boolean positivo(BigDecimal v) {
        return v != null && v.signum() > 0;
    }

    private static boolean negativo(BigDecimal v) {
        return v != null && v.signum() < 0;
    }
}
