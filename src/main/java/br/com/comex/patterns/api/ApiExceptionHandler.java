package br.com.comex.patterns.api;

import br.com.comex.patterns.core.model.TransicaoStatusInvalidaException;
import br.com.comex.patterns.core.validacao.ValidacaoImportacaoException;
import br.com.comex.patterns.infra.cambio.CotacaoIndisponivelException;
import br.com.comex.patterns.service.ProcessoNaoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

/** Converte exceções do domínio em respostas HTTP no formato Problem Details (RFC 9457). */
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(ValidacaoImportacaoException.class)
    ProblemDetail validacao(ValidacaoImportacaoException e) {
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY,
                "A importação tem pendências.");
        problema.setTitle("Importação inválida");
        problema.setProperty("erros", e.getErros());
        return problema;
    }

    @ExceptionHandler(TransicaoStatusInvalidaException.class)
    ProblemDetail transicao(TransicaoStatusInvalidaException e) {
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, e.getMessage());
        problema.setTitle("Transição de status inválida");
        return problema;
    }

    @ExceptionHandler(ProcessoNaoEncontradoException.class)
    ProblemDetail naoEncontrado(ProcessoNaoEncontradoException e) {
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
        problema.setTitle("Processo não encontrado");
        return problema;
    }

    @ExceptionHandler(CotacaoIndisponivelException.class)
    ProblemDetail cotacao(CotacaoIndisponivelException e) {
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(HttpStatus.SERVICE_UNAVAILABLE, e.getMessage());
        problema.setTitle("Cotação indisponível");
        return problema;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ProblemDetail camposInvalidos(MethodArgumentNotValidException e) {
        List<String> erros = e.getBindingResult().getFieldErrors().stream()
                .map(f -> f.getField() + ": " + f.getDefaultMessage())
                .toList();
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Campos inválidos.");
        problema.setTitle("Requisição inválida");
        problema.setProperty("erros", erros);
        return problema;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ProblemDetail jsonInvalido(HttpMessageNotReadableException e) {
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
                "JSON inválido ou valor fora dos permitidos (confira os enums: incoterm, regime, status).");
        problema.setTitle("Requisição inválida");
        return problema;
    }
}
