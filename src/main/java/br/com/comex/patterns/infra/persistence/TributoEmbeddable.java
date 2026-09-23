package br.com.comex.patterns.infra.persistence;

import br.com.comex.patterns.core.model.TipoTributo;
import br.com.comex.patterns.core.model.TributoCalculado;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.math.BigDecimal;

@Embeddable
public class TributoEmbeddable {

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private TipoTributo tipo;

    @Column(precision = 19, scale = 2)
    private BigDecimal baseCalculo;

    @Column(precision = 5, scale = 2)
    private BigDecimal aliquota;

    @Column(precision = 19, scale = 2)
    private BigDecimal valorIntegral;

    @Column(precision = 19, scale = 2)
    private BigDecimal valorDevido;

    @Column(precision = 19, scale = 2)
    private BigDecimal valorSuspenso;

    protected TributoEmbeddable() {
    }

    public static TributoEmbeddable de(TributoCalculado t) {
        TributoEmbeddable e = new TributoEmbeddable();
        e.tipo = t.tipo();
        e.baseCalculo = t.baseCalculo();
        e.aliquota = t.aliquota();
        e.valorIntegral = t.valorIntegral();
        e.valorDevido = t.valorDevido();
        e.valorSuspenso = t.valorSuspenso();
        return e;
    }

    public TributoCalculado toTributo() {
        return new TributoCalculado(tipo, baseCalculo, aliquota, valorIntegral, valorDevido, valorSuspenso);
    }
}
