package com.engeman.imoveis.repository;

import com.engeman.imoveis.entity.Favorito;
import com.engeman.imoveis.entity.User;
import com.engeman.imoveis.entity.Imovel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoritoRepository extends JpaRepository<Favorito, Long> {
    boolean existsByClienteAndImovel(User cliente, Imovel imovel);
    Optional<Favorito> findByClienteAndImovel(User cliente, Imovel imovel);
    List<Favorito> findByCliente(User cliente);
}