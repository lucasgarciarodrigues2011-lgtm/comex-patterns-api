package br.com.comex.patterns.core.validacao;

import java.util.List;

public class ValidacaoImportacaoException extends RuntimeException {

    private final List<String> erros;

    public ValidacaoImportacaoException(List<String> erros) {
        super("Importação inválida: " + String.join("; ", erros));
        this.erros = List.copyOf(erros);
    }

    public List<String> getErros() {
        return erros;
    }
}
