package com.engeman.imoveis.dto.imovel;

import com.engeman.imoveis.entity.Imovel;
import com.engeman.imoveis.enums.TipoImovel;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ImovelResponse {

    private Long id;
    private String titulo;
    private String descricao;
    private TipoImovel tipo;
    private BigDecimal preco;
    private Integer quartos;
    private Integer banheiros;
    private BigDecimal areaM2;
    private String endereco;
    private String cidade;
    private String estado;
    private boolean ativo;
    private Long corretorId;
    private String corretorNome;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ImovelResponse(Imovel imovel) {
        this.id = imovel.getId();
        this.titulo = imovel.getTitulo();
        this.descricao = imovel.getDescricao();
        this.tipo = imovel.getTipo();
        this.preco = imovel.getPreco();
        this.quartos = imovel.getQuartos();
        this.banheiros = imovel.getBanheiros();
        this.areaM2 = imovel.getAreaM2();
        this.endereco = imovel.getEndereco();
        this.cidade = imovel.getCidade();
        this.estado = imovel.getEstado();
        this.ativo = imovel.isAtivo();
        this.corretorId = imovel.getCorretor().getId();
        this.corretorNome = imovel.getCorretor().getName();
        this.createdAt = imovel.getCreatedAt();
        this.updatedAt = imovel.getUpdatedAt();
    }

    public Long getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getDescricao() { return descricao; }
    public TipoImovel getTipo() { return tipo; }
    public BigDecimal getPreco() { return preco; }
    public Integer getQuartos() { return quartos; }
    public Integer getBanheiros() { return banheiros; }
    public BigDecimal getAreaM2() { return areaM2; }
    public String getEndereco() { return endereco; }
    public String getCidade() { return cidade; }
    public String getEstado() { return estado; }
    public boolean isAtivo() { return ativo; }
    public Long getCorretorId() { return corretorId; }
    public String getCorretorNome() { return corretorNome; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}