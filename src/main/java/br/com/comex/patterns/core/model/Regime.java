package br.com.comex.patterns.core.model;

/**
 * Regimes aduaneiros suportados pela simulação.
 * Cada regime possui uma estratégia de cálculo própria (padrão Strategy).
 */
public enum Regime {
    COMUM("Importação comum (nacionalização)"),
    ADMISSAO_TEMPORARIA_SUSPENSAO_TOTAL("Admissão Temporária com suspensão total (ex.: feiras e eventos)"),
    ADMISSAO_TEMPORARIA_UTILIZACAO_ECONOMICA("Admissão Temporária para utilização econômica (pagamento proporcional)"),
    DRAWBACK_SUSPENSAO("Drawback modalidade suspensão");

    private final String descricao;

    Regime(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
