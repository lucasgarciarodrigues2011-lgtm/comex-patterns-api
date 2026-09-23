package br.com.comex.patterns.core.port;

import java.math.BigDecimal;

/** Porta de saída para obter a taxa de câmbio de uma moeda em reais. */
public interface CotacaoCambio {

    BigDecimal taxaVenda(String moeda);
}
