package br.com.comex.patterns.core.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Resultado imutável de uma simulação de tributos.
 * Construído com o padrão Builder, já que tem muitos campos e os totais são derivados dos tributos.
 */
public final class ResultadoTributos {

    private final Regime regime;
    private final BigDecimal taxaCambio;
    private final BigDecimal valorAduaneiro;
    private final List<TributoCalculado> tributos;
    private final BigDecimal totalDevido;
    private final BigDecimal totalSuspenso;
    private final BigDecimal despesasAduaneiras;
    private final BigDecimal custoTotalDesembaraco;
    private final List<String> observacoes;

    private ResultadoTributos(Builder b) {
        this.regime = b.regime;
        this.taxaCambio = b.taxaCambio;
        this.valorAduaneiro = b.valorAduaneiro;
        this.tributos = List.copyOf(b.tributos);
        this.despesasAduaneiras = b.despesasAduaneiras;
        this.totalDevido = tributos.stream().map(TributoCalculado::valorDevido)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        this.totalSuspenso = tributos.stream().map(TributoCalculado::valorSuspenso)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        this.custoTotalDesembaraco = valorAduaneiro.add(totalDevido).add(despesasAduaneiras);
        this.observacoes = Collections.unmodifiableList(new ArrayList<>(b.observacoes));
    }

    public static Builder builder() {
        return new Builder();
    }

    public Regime getRegime() { return regime; }
    public BigDecimal getTaxaCambio() { return taxaCambio; }
    public BigDecimal getValorAduaneiro() { return valorAduaneiro; }
    public List<TributoCalculado> getTributos() { return tributos; }
    public BigDecimal getTotalDevido() { return totalDevido; }
    public BigDecimal getTotalSuspenso() { return totalSuspenso; }
    public BigDecimal getDespesasAduaneiras() { return despesasAduaneiras; }
    public BigDecimal getCustoTotalDesembaraco() { return custoTotalDesembaraco; }
    public List<String> getObservacoes() { return observacoes; }

    public TributoCalculado tributo(TipoTributo tipo) {
        return tributos.stream().filter(t -> t.tipo() == tipo).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Tributo não calculado: " + tipo));
    }

    public static final class Builder {
        private Regime regime;
        private BigDecimal taxaCambio;
        private BigDecimal valorAduaneiro;
        private BigDecimal despesasAduaneiras = BigDecimal.ZERO;
        private final List<TributoCalculado> tributos = new ArrayList<>();
        private final List<String> observacoes = new ArrayList<>();

        private Builder() {
        }

        public Builder regime(Regime regime) {
            this.regime = regime;
            return this;
        }

        public Builder taxaCambio(BigDecimal taxaCambio) {
            this.taxaCambio = taxaCambio;
            return this;
        }

        public Builder valorAduaneiro(BigDecimal valorAduaneiro) {
            this.valorAduaneiro = valorAduaneiro;
            return this;
        }

        public Builder despesasAduaneiras(BigDecimal despesasAduaneiras) {
            this.despesasAduaneiras = despesasAduaneiras == null ? BigDecimal.ZERO : despesasAduaneiras;
            return this;
        }

        public Builder tributo(TributoCalculado tributo) {
            this.tributos.add(tributo);
            return this;
        }

        public Builder observacao(String observacao) {
            if (observacao != null && !observacao.isBlank()) {
                this.observacoes.add(observacao);
            }
            return this;
        }

        public ResultadoTributos build() {
            Objects.requireNonNull(regime, "regime é obrigatório");
            Objects.requireNonNull(taxaCambio, "taxaCambio é obrigatória");
            Objects.requireNonNull(valorAduaneiro, "valorAduaneiro é obrigatório");
            if (tributos.isEmpty()) {
                throw new IllegalStateException("Nenhum tributo informado");
            }
            return new ResultadoTributos(this);
        }
    }
}
