package br.com.comex.patterns.api;

import br.com.comex.patterns.api.dto.ImportacaoRequest;
import br.com.comex.patterns.api.dto.SimulacaoResponse;
import br.com.comex.patterns.service.ImportacaoFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/simulacoes")
@Tag(name = "Simulações", description = "Cálculo de tributos sem abrir processo")
public class SimulacaoController {

    private final ImportacaoFacade facade;

    public SimulacaoController(ImportacaoFacade facade) {
        this.facade = facade;
    }

    @PostMapping
    @Operation(summary = "Simula os tributos de uma importação no regime informado")
    public SimulacaoResponse simular(@RequestBody ImportacaoRequest request) {
        return SimulacaoResponse.de(facade.simular(request.toDados()));
    }
}
