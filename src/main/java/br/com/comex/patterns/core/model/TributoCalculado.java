package br.com.comex.patterns.core.model;

import java.math.BigDecimal;

public record TributoCalculado(TipoTributo tipo,
                               BigDecimal baseCalculo,
                               BigDecimal aliquota,
                               BigDecimal valorIntegral,
                               BigDecimal valorDevido,
                               BigDecimal valorSuspenso) {
}
