package br.com.comex.patterns.core.model;

public class TransicaoStatusInvalidaException extends RuntimeException {

    public TransicaoStatusInvalidaException(StatusProcesso origem, StatusProcesso destino) {
        super("Transição inválida: %s -> %s. Permitidos a partir de %s: %s"
                .formatted(origem, destino, origem, origem.proximosPermitidos()));
    }
}
