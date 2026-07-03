package com.engeman.imoveis.repository;

import com.engeman.imoveis.entity.Imovel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ImovelRepository extends JpaRepository<Imovel, Long>, JpaSpecificationExecutor<Imovel> {

    List<Imovel> findByCorretorId(Long corretorId);
}