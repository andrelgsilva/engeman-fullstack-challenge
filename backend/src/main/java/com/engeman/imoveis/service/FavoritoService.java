package com.engeman.imoveis.service;

import com.engeman.imoveis.dto.favorito.FavoritoResponse;
import com.engeman.imoveis.entity.Favorito;
import com.engeman.imoveis.entity.Imovel;
import com.engeman.imoveis.entity.User;
import com.engeman.imoveis.exception.BusinessException;
import com.engeman.imoveis.exception.ResourceNotFoundException;
import com.engeman.imoveis.repository.FavoritoRepository;
import com.engeman.imoveis.repository.ImovelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoritoService {

    private final FavoritoRepository favoritoRepository;
    private final ImovelRepository imovelRepository;

    public FavoritoResponse adicionar(Long imovelId, User cliente) {
        Imovel imovel = imovelRepository.findById(imovelId)
                .orElseThrow(() -> new ResourceNotFoundException("Imóvel", "id", imovelId));

        if (!imovel.isAtivo()) {
            throw new BusinessException("Não é possível favoritar um imóvel inativo");
        }

        if (favoritoRepository.existsByClienteAndImovel(cliente, imovel)) {
            throw new BusinessException("Imóvel já está nos favoritos");
        }

        Favorito favorito = Favorito.builder()
                .cliente(cliente)
                .imovel(imovel)
                .build();

        return new FavoritoResponse(favoritoRepository.save(favorito));
    }

    public void remover(Long imovelId, User cliente) {
        Imovel imovel = imovelRepository.findById(imovelId)
                .orElseThrow(() -> new ResourceNotFoundException("Imóvel", "id", imovelId));

        Favorito favorito = favoritoRepository.findByClienteAndImovel(cliente, imovel)
                .orElseThrow(() -> new ResourceNotFoundException("Favorito", "imovelId", imovelId));

        favoritoRepository.delete(favorito);
    }

    public List<FavoritoResponse> listar(User cliente) {
        return favoritoRepository.findByCliente(cliente)
                .stream()
                .map(FavoritoResponse::new)
                .toList();
    }
}