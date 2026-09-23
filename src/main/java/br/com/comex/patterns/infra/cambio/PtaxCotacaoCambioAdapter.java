package br.com.comex.patterns.infra.cambio;

import br.com.comex.patterns.core.port.CotacaoCambio;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

/**
 * Padrão Adapter para a API pública PTAX do Banco Central (dados abertos, sem autenticação).
 * Faz o papel que o ViaCEP + OpenFeign fazem no lab de referência: integrar um serviço externo
 * atrás de uma interface do domínio ({@link CotacaoCambio}).
 * <p>
 * Usa a cotação de venda do boletim de fechamento mais recente dos últimos dias.
 */
@Component
public class PtaxCotacaoCambioAdapter implements CotacaoCambio {

    private static final Logger log = LoggerFactory.getLogger(PtaxCotacaoCambioAdapter.class);
    private static final DateTimeFormatter FORMATO_BCB = DateTimeFormatter.ofPattern("MM-dd-yyyy");

    private final RestClient restClient;
    private final String urlBase;

    public PtaxCotacaoCambioAdapter(RestClient.Builder builder,
                                    @Value("${comex.ptax.url-base}") String urlBase) {
        this.restClient = builder.build();
        this.urlBase = urlBase;
    }

    @Override
    public BigDecimal taxaVenda(String moeda) {
        LocalDate hoje = LocalDate.now();
        String url = urlBase
                + "/CotacaoMoedaPeriodo(moeda=@moeda,dataInicial=@dataInicial,dataFinalCotacao=@dataFinalCotacao)"
                + "?@moeda='" + moeda + "'"
                + "&@dataInicial='" + hoje.minusDays(10).format(FORMATO_BCB) + "'"
                + "&@dataFinalCotacao='" + hoje.format(FORMATO_BCB) + "'"
                + "&$format=json";
        try {
            RespostaPtax resposta = restClient.get().uri(URI.create(url)).retrieve().body(RespostaPtax.class);
            List<CotacaoPtax> cotacoes = resposta == null || resposta.value() == null ? List.of() : resposta.value();
            CotacaoPtax escolhida = cotacoes.stream()
                    .filter(c -> "Fechamento".equalsIgnoreCase(c.tipoBoletim()))
                    .max(Comparator.comparing(CotacaoPtax::dataHoraCotacao))
                    .or(() -> cotacoes.stream().max(Comparator.comparing(CotacaoPtax::dataHoraCotacao)))
                    .orElseThrow(() -> new IllegalStateException("Nenhuma cotação retornada"));
            log.info("PTAX {} = {} ({} {})", moeda, escolhida.cotacaoVenda(),
                    escolhida.tipoBoletim(), escolhida.dataHoraCotacao());
            return escolhida.cotacaoVenda();
        } catch (RuntimeException e) {
            throw new CotacaoIndisponivelException(moeda, e);
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record RespostaPtax(List<CotacaoPtax> value) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record CotacaoPtax(BigDecimal cotacaoVenda, String dataHoraCotacao, String tipoBoletim) {
    }
}
