package br.com.comex.patterns.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AbrirProcessoRequest(
        @Schema(example = "IMP-2026-0001") @NotBlank @Size(max = 40) String referencia,
        @Schema(example = "Mineradora Exemplo Ltda") @NotBlank String importador,
        @NotNull @Valid ImportacaoRequest importacao) {
}
