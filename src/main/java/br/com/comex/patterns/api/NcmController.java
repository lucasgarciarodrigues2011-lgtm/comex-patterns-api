package br.com.comex.patterns.api;

import br.com.comex.patterns.core.model.NcmInfo;
import br.com.comex.patterns.infra.persistence.NcmEntity;
import br.com.comex.patterns.infra.persistence.NcmRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/ncms")
@Tag(name = "NCMs", description = "Catálogo de NCMs com alíquotas ilustrativas")
public class NcmController {

    private final NcmRepository repository;

    public NcmController(NcmRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    @Operation(summary = "Lista as NCMs disponíveis para simulação")
    public List<NcmInfo> listar() {
        return repository.findAll(Sort.by("codigo")).stream().map(NcmEntity::toInfo).toList();
    }
}
