package br.com.comex.patterns.api.dto;

import br.com.comex.patterns.core.model.DadosImportacao;
import br.com.comex.patterns.core.model.Incoterm;
import br.com.comex.patterns.core.model.Regime;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

/**
 * Dados de uma importação. As regras de negócio são validadas pela cadeia de validação do domínio
 * (Chain of Responsibility), que devolve todas as pendências de uma vez.
 */
public record ImportacaoRequest(
        @Schema(example = "8474.20.10") String ncm,
        @Schema(example = "FOB") Incoterm incoterm,
        @Schema(example = "USD") String moeda,
        @Schema(description = "Opcional. Se omitida, usa a PTAX de venda do Banco Central.", example = "5.00")
        BigDecimal taxaCambio,
        @Schema(example = "10000.00") BigDecimal valorMercadoria,
        @Schema(example = "1000.00") BigDecimal frete,
        @Schema(example = "100.00") BigDecimal seguro,
        @Schema(description = "Em reais: taxa Siscomex, AFRMM, armazenagem etc.", example = "500.00")
        BigDecimal despesasAduaneiras,
        @Schema(description = "Alíquota de ICMS do estado de destino, em %", example = "18")
        BigDecimal aliquotaIcms,
        @Schema(example = "COMUM") Regime regime,
        @Schema(description = "Só para Admissão Temporária (em meses)") Integer mesesPermanencia,
        @Schema(example = "false") boolean possuiLicenca) {

    public DadosImportacao toDados() {
        return new DadosImportacao(ncm, incoterm, moeda, taxaCambio, valorMercadoria, frete, seguro,
                despesasAduaneiras, aliquotaIcms, regime, mesesPermanencia, possuiLicenca);
    }
}
