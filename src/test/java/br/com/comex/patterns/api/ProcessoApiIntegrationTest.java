package br.com.comex.patterns.api;

import br.com.comex.patterns.core.port.CotacaoCambio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ProcessoApiIntegrationTest {

    private static final String IMPORTACAO_FOB = """
            {
              "ncm": "8474.20.10", "incoterm": "FOB", "moeda": "USD", "taxaCambio": 5.00,
              "valorMercadoria": 10000, "frete": 1000, "seguro": 100,
              "despesasAduaneiras": 500, "aliquotaIcms": 18, "regime": "COMUM"
            }
            """;

    @Autowired
    private MockMvc mvc;

    /** A PTAX real não é chamada nos testes. */
    @MockitoBean
    private CotacaoCambio cotacaoCambio;

    @Test
    @DisplayName("POST /api/simulacoes calcula os tributos")
    void simula() throws Exception {
        mvc.perform(post("/api/simulacoes").contentType(MediaType.APPLICATION_JSON).content(IMPORTACAO_FOB))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valorAduaneiro").value(55500.00))
                .andExpect(jsonPath("$.totalDevido").value(37436.89))
                .andExpect(jsonPath("$.tributos", hasSize(5)));
    }

    @Test
    @DisplayName("Sem taxa de câmbio, usa a cotação PTAX")
    void usaPtax() throws Exception {
        given(cotacaoCambio.taxaVenda("USD")).willReturn(new BigDecimal("5.25"));
        String semTaxa = IMPORTACAO_FOB.replace("\"taxaCambio\": 5.00,", "");

        mvc.perform(post("/api/simulacoes").contentType(MediaType.APPLICATION_JSON).content(semTaxa))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.taxaCambio").value(5.25))
                .andExpect(jsonPath("$.valorAduaneiro").value(58275.00));
    }

    @Test
    @DisplayName("Regras de negócio violadas retornam 422 com a lista de erros")
    void validacao() throws Exception {
        String cif = IMPORTACAO_FOB.replace("FOB", "CIF");
        mvc.perform(post("/api/simulacoes").contentType(MediaType.APPLICATION_JSON).content(cif))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.erros", hasSize(2)));
    }

    @Test
    @DisplayName("Ciclo completo: abrir, avançar status, bloquear salto inválido e consultar histórico")
    void cicloDoProcesso() throws Exception {
        String abrir = """
                {"referencia": "IMP-TESTE-001", "importador": "Mineradora Exemplo Ltda", "importacao": %s}
                """.formatted(IMPORTACAO_FOB);

        String location = mvc.perform(post("/api/processos").contentType(MediaType.APPLICATION_JSON).content(abrir))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.status").value("ABERTO"))
                .andReturn().getResponse().getHeader("Location");

        mvc.perform(patch(location + "/status").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\": \"REGISTRADO\", \"observacao\": \"DUIMP registrada\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REGISTRADO"));

        mvc.perform(patch(location + "/status").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\": \"ENTREGUE\"}"))
                .andExpect(status().isConflict());

        mvc.perform(get(location + "/historico"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[1].statusNovo").value("REGISTRADO"));

        mvc.perform(post("/api/processos").contentType(MediaType.APPLICATION_JSON).content(abrir))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @DisplayName("Processo inexistente retorna 404")
    void naoEncontrado() throws Exception {
        mvc.perform(get("/api/processos/9999")).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Catálogo de NCMs vem do data.sql")
    void ncms() throws Exception {
        mvc.perform(get("/api/ncms")).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(6)));
    }
}
