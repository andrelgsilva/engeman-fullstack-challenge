package com.engeman.imoveis.repository;

import com.engeman.imoveis.entity.Imovel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ImovelRepository extends JpaRepository<Imovel, Long>, JpaSpecificationExecutor<Imovel> {
}