package br.com.comex.patterns.api;

import br.com.comex.patterns.api.dto.AbrirProcessoRequest;
import br.com.comex.patterns.api.dto.AlterarStatusRequest;
import br.com.comex.patterns.api.dto.HistoricoResponse;
import br.com.comex.patterns.api.dto.ProcessoResponse;
import br.com.comex.patterns.core.model.StatusProcesso;
import br.com.comex.patterns.infra.persistence.ProcessoImportacao;
import br.com.comex.patterns.service.ImportacaoFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/processos")
@Tag(name = "Processos", description = "Processos de importação e seu ciclo de vida")
public class ProcessoController {

    private final ImportacaoFacade facade;

    public ProcessoController(ImportacaoFacade facade) {
        this.facade = facade;
    }

    @PostMapping
    @Operation(summary = "Abre um processo: valida, calcula os tributos e grava com status ABERTO")
    public ResponseEntity<ProcessoResponse> abrir(@RequestBody @Valid AbrirProcessoRequest request) {
        ProcessoImportacao processo = facade.abrirProcesso(request.referencia(), request.importador(),
                request.importacao().toDados());
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(processo.getId()).toUri();
        return ResponseEntity.created(location).body(ProcessoResponse.de(processo));
    }

    @GetMapping
    @Operation(summary = "Lista processos, opcionalmente filtrando por status")
    public List<ProcessoResponse> listar(@RequestParam(required = false) StatusProcesso status) {
        return facade.listar(status).stream().map(ProcessoResponse::de).toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um processo pelo id")
    public ProcessoResponse buscar(@PathVariable Long id) {
        return ProcessoResponse.de(facade.buscar(id));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Avança o status do processo (só transições permitidas; dispara os observers)")
    public ProcessoResponse alterarStatus(@PathVariable Long id, @RequestBody @Valid AlterarStatusRequest request) {
        return ProcessoResponse.de(facade.alterarStatus(id, request.status(), request.observacao()));
    }

    @GetMapping("/{id}/historico")
    @Operation(summary = "Trilha de auditoria das mudanças de status")
    public List<HistoricoResponse> historico(@PathVariable Long id) {
        return facade.historico(id).stream().map(HistoricoResponse::de).toList();
    }
}
