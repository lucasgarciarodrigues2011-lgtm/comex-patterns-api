package br.com.comex.patterns.core.model;

/**
 * Incoterms 2020. Indicam se o valor da mercadoria já inclui frete e seguro internacionais,
 * o que afeta a composição do valor aduaneiro.
 */
public enum Incoterm {
    EXW(false, false),
    FCA(false, false),
    FAS(false, false),
    FOB(false, false),
    CFR(true, false),
    CPT(true, false),
    CIF(true, true),
    CIP(true, true),
    DAP(true, true),
    DPU(true, true),
    DDP(true, true);

    private final boolean freteIncluso;
    private final boolean seguroIncluso;

    Incoterm(boolean freteIncluso, boolean seguroIncluso) {
        this.freteIncluso = freteIncluso;
        this.seguroIncluso = seguroIncluso;
    }

    public boolean isFreteIncluso() {
        return freteIncluso;
    }

    public boolean isSeguroIncluso() {
        return seguroIncluso;
    }
}
