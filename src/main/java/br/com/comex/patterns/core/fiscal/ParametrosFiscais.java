package br.com.comex.patterns.core.fiscal;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Padrão Singleton na forma recomendada por Joshua Bloch (Effective Java): um enum de uma única instância.
 * É thread-safe, resistente a serialização e a reflexão, sem precisar de Spring.
 * <p>
 * Guarda parâmetros fiscais gerais usados em todas as simulações.
 */
public enum ParametrosFiscais {
    INSTANCIA;

    /** Alíquota geral do PIS-Importação (Lei 10.865/2004, redação da Lei 13.137/2015), em %. */
    private final BigDecimal aliquotaPis = new BigDecimal("2.10");
    /** Alíquota geral da COFINS-Importação, em %. */
    private final BigDecimal aliquotaCofins = new BigDecimal("9.65");
    /** Percentual devido por mês na Admissão Temporária para utilização econômica (Lei 9.430/96, art. 79). */
    private final BigDecimal percentualMensalUtilizacaoEconomica = new BigDecimal("1.00");

    private final int escala = 2;
    private final RoundingMode arredondamento = RoundingMode.HALF_UP;

    public BigDecimal aliquotaPis() {
        return aliquotaPis;
    }

    public BigDecimal aliquotaCofins() {
        return aliquotaCofins;
    }

    public BigDecimal percentualMensalUtilizacaoEconomica() {
        return percentualMensalUtilizacaoEconomica;
    }

    public BigDecimal arredondar(BigDecimal valor) {
        return valor.setScale(escala, arredondamento);
    }

    /** Aplica um percentual (ex.: 14.00) sobre um valor e arredonda para centavos. */
    public BigDecimal aplicarPercentual(BigDecimal valor, BigDecimal percentual) {
        return arredondar(valor.multiply(percentual).divide(BigDecimal.valueOf(100), 10, arredondamento));
    }
}
