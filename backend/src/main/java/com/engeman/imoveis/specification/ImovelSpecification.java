package com.engeman.imoveis.specification;

import com.engeman.imoveis.entity.Imovel;
import com.engeman.imoveis.enums.TipoImovel;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class ImovelSpecification {

    public static Specification<Imovel> tituloContains(String titulo) {
        return (root, query, cb) ->
                titulo == null ? null :
                cb.like(cb.lower(root.get("titulo")), "%" + titulo.toLowerCase() + "%");
    }

    public static Specification<Imovel> tipoEquals(TipoImovel tipo) {
        return (root, query, cb) ->
                tipo == null ? null :
                cb.equal(root.get("tipo"), tipo);
    }

    public static Specification<Imovel> precoMinimo(BigDecimal precoMin) {
        return (root, query, cb) ->
                precoMin == null ? null :
                cb.greaterThanOrEqualTo(root.get("preco"), precoMin);
    }

    public static Specification<Imovel> precoMaximo(BigDecimal precoMax) {
        return (root, query, cb) ->
                precoMax == null ? null :
                cb.lessThanOrEqualTo(root.get("preco"), precoMax);
    }

    public static Specification<Imovel> quartosMinimos(Integer quartos) {
        return (root, query, cb) ->
                quartos == null ? null :
                cb.greaterThanOrEqualTo(root.get("quartos"), quartos);
    }

    public static Specification<Imovel> apenasAtivos() {
        return (root, query, cb) ->
                cb.isTrue(root.get("ativo"));
    }
}