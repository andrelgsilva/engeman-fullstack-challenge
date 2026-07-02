package com.engeman.imoveis.dto.imovel;

import jakarta.validation.constraints.NotNull;

public class UpdateImovelStatusRequest {

    @NotNull(message = "Status é obrigatório")
    private Boolean ativo;

    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }
}