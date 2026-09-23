package br.com.comex.patterns.core.model;

import java.util.EnumSet;
import java.util.Set;

/**
 * Ciclo de vida de um processo de importação.
 * Cada status conhece as transições permitidas a partir dele (uma forma enxuta do padrão State),
 * o que impede, por exemplo, desembaraçar um processo que ainda nem foi registrado.
 */
public enum StatusProcesso {
    ABERTO,
    REGISTRADO,
    CANAL_VERDE,
    CANAL_AMARELO,
    CANAL_VERMELHO,
    CANAL_CINZA,
    EM_EXIGENCIA,
    DESEMBARACADO,
    ENTREGUE,
    CANCELADO;

    public Set<StatusProcesso> proximosPermitidos() {
        return switch (this) {
            case ABERTO -> EnumSet.of(REGISTRADO, CANCELADO);
            case REGISTRADO -> EnumSet.of(CANAL_VERDE, CANAL_AMARELO, CANAL_VERMELHO, CANAL_CINZA);
            case CANAL_VERDE -> EnumSet.of(DESEMBARACADO);
            case CANAL_AMARELO, CANAL_VERMELHO, CANAL_CINZA -> EnumSet.of(EM_EXIGENCIA, DESEMBARACADO);
            case EM_EXIGENCIA -> EnumSet.of(DESEMBARACADO);
            case DESEMBARACADO -> EnumSet.of(ENTREGUE);
            case ENTREGUE, CANCELADO -> EnumSet.noneOf(StatusProcesso.class);
        };
    }

    public boolean podeIrPara(StatusProcesso destino) {
        return proximosPermitidos().contains(destino);
    }

    public StatusProcesso transicionarPara(StatusProcesso destino) {
        if (!podeIrPara(destino)) {
            throw new TransicaoStatusInvalidaException(this, destino);
        }
        return destino;
    }
}
