package com.engeman.imoveis.dto.imovel;

import com.engeman.imoveis.enums.TipoImovel;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public class UpdateImovelRequest {

    @NotBlank(message = "Título é obrigatório")
    private String titulo;

    private String descricao;

    @NotNull(message = "Tipo é obrigatório")
    private TipoImovel tipo;

    @NotNull(message = "Preço é obrigatório")
    @Positive(message = "Preço deve ser maior que zero")
    private BigDecimal preco;

    @NotNull(message = "Quantidade de quartos é obrigatória")
    @PositiveOrZero(message = "Quartos não pode ser negativo")
    private Integer quartos;

    @PositiveOrZero(message = "Banheiros não pode ser negativo")
    private Integer banheiros;

    @PositiveOrZero(message = "Área não pode ser negativa")
    private BigDecimal areaM2;

    @NotBlank(message = "Endereço é obrigatório")
    private String endereco;

    @NotBlank(message = "Cidade é obrigatória")
    private String cidade;

    @NotBlank(message = "Estado é obrigatório")
    @Size(min = 2, max = 2, message = "Estado deve ter 2 caracteres (UF)")
    private String estado;

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public TipoImovel getTipo() { return tipo; }
    public void setTipo(TipoImovel tipo) { this.tipo = tipo; }

    public BigDecimal getPreco() { return preco; }
    public void setPreco(BigDecimal preco) { this.preco = preco; }

    public Integer getQuartos() { return quartos; }
    public void setQuartos(Integer quartos) { this.quartos = quartos; }

    public Integer getBanheiros() { return banheiros; }
    public void setBanheiros(Integer banheiros) { this.banheiros = banheiros; }

    public BigDecimal getAreaM2() { return areaM2; }
    public void setAreaM2(BigDecimal areaM2) { this.areaM2 = areaM2; }

    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }

    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}