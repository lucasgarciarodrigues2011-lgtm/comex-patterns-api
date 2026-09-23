package br.com.comex.patterns.core.fiscal;

import br.com.comex.patterns.core.model.Regime;

import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;

/**
 * Padrão Factory: entrega a estratégia correta para um regime.
 * Recebe todas as estratégias disponíveis (no Spring, injetadas automaticamente como lista)
 * e as indexa por regime, evitando if/else ou switch espalhados pelo código.
 */
public class EstrategiaTributariaFactory {

    private final Map<Regime, CalculoTributarioStrategy> estrategias = new EnumMap<>(Regime.class);

    public EstrategiaTributariaFactory(Collection<? extends CalculoTributarioStrategy> disponiveis) {
        for (CalculoTributarioStrategy estrategia : disponiveis) {
            CalculoTributarioStrategy anterior = estrategias.put(estrategia.regime(), estrategia);
            if (anterior != null) {
                throw new IllegalStateException("Mais de uma estratégia para o regime " + estrategia.regime());
            }
        }
    }

    public CalculoTributarioStrategy para(Regime regime) {
        CalculoTributarioStrategy estrategia = estrategias.get(regime);
        if (estrategia == null) {
            throw new IllegalArgumentException("Nenhuma estratégia cadastrada para o regime " + regime);
        }
        return estrategia;
    }
}
