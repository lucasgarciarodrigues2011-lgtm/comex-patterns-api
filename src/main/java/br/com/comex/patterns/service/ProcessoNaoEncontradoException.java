package br.com.comex.patterns.service;

public class ProcessoNaoEncontradoException extends RuntimeException {

    public ProcessoNaoEncontradoException(Long id) {
        super("Processo %d não encontrado.".formatted(id));
    }
}
