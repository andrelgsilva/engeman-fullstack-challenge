package com.engeman.imoveis.dto.favorito;

import com.engeman.imoveis.entity.Favorito;
import com.engeman.imoveis.enums.TipoImovel;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class FavoritoResponse {

    private Long favoritoId;
    private Long imovelId;
    private String titulo;
    private TipoImovel tipo;
    private BigDecimal preco;
    private Integer quartos;
    private String cidade;
    private String estado;
    private LocalDateTime favoritadoEm;

    public FavoritoResponse(Favorito favorito) {
        this.favoritoId = favorito.getId();
        this.imovelId = favorito.getImovel().getId();
        this.titulo = favorito.getImovel().getTitulo();
        this.tipo = favorito.getImovel().getTipo();
        this.preco = favorito.getImovel().getPreco();
        this.quartos = favorito.getImovel().getQuartos();
        this.cidade = favorito.getImovel().getCidade();
        this.estado = favorito.getImovel().getEstado();
        this.favoritadoEm = favorito.getCreatedAt();
    }

    public Long getFavoritoId() { return favoritoId; }
    public Long getImovelId() { return imovelId; }
    public String getTitulo() { return titulo; }
    public TipoImovel getTipo() { return tipo; }
    public BigDecimal getPreco() { return preco; }
    public Integer getQuartos() { return quartos; }
    public String getCidade() { return cidade; }
    public String getEstado() { return estado; }
    public LocalDateTime getFavoritadoEm() { return favoritadoEm; }
}