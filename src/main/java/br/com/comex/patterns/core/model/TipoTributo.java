package br.com.comex.patterns.core.model;

public enum TipoTributo {
    II("Imposto de Importação", true),
    IPI("Imposto sobre Produtos Industrializados", true),
    PIS("PIS/PASEP-Importação", true),
    COFINS("COFINS-Importação", true),
    ICMS("ICMS-Importação", false);

    private final String descricao;
    private final boolean federal;

    TipoTributo(String descricao, boolean federal) {
        this.descricao = descricao;
        this.federal = federal;
    }

    public String getDescricao() {
        return descricao;
    }

    public boolean isFederal() {
        return federal;
    }
}
