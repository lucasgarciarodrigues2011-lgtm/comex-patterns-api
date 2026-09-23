package br.com.comex.patterns.api.dto;

import br.com.comex.patterns.core.model.StatusProcesso;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record AlterarStatusRequest(
        @Schema(example = "REGISTRADO") @NotNull StatusProcesso status,
        @Schema(example = "DUIMP registrada") String observacao) {
}
