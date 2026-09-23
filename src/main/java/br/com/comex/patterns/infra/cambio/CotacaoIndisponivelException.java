package br.com.comex.patterns.infra.cambio;

public class CotacaoIndisponivelException extends RuntimeException {

    public CotacaoIndisponivelException(String moeda, Throwable causa) {
        super("Não foi possível obter a cotação PTAX de %s. Informe a taxaCambio manualmente.".formatted(moeda), causa);
    }
}
