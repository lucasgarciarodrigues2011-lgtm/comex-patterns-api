package br.com.comex.patterns.core.model;

import java.math.BigDecimal;

/**
 * Dados de entrada de uma importação. Valores de mercadoria, frete e seguro na moeda estrangeira;
 * despesas aduaneiras (taxa Siscomex, AFRMM etc.) em reais; alíquota de ICMS em percentual.
 */
public record DadosImportacao(String ncm,
                              Incoterm incoterm,
                              String moeda,
                              BigDecimal taxaCambio,
                              BigDecimal valorMercadoria,
                              BigDecimal frete,
                              BigDecimal seguro,
                              BigDecimal despesasAduaneiras,
                              BigDecimal aliquotaIcms,
                              Regime regime,
                              Integer mesesPermanencia,
                              boolean possuiLicenca) {

    /** Mesma importação com outra taxa de câmbio (usado quando a taxa vem da PTAX). */
    public DadosImportacao comTaxaCambio(BigDecimal novaTaxa) {
        return new DadosImportacao(ncm, incoterm, moeda, novaTaxa, valorMercadoria, frete, seguro,
                despesasAduaneiras, aliquotaIcms, regime, mesesPermanencia, possuiLicenca);
    }
}
