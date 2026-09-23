package br.com.comex.patterns.core.model;

import java.math.BigDecimal;

/**
 * Dados tributários de uma NCM. Alíquotas em percentual (ex.: 14.00 = 14%).
 */
public record NcmInfo(String codigo,
                      String descricao,
                      BigDecimal aliquotaIi,
                      BigDecimal aliquotaIpi,
                      boolean exigeLicenca) {
}
