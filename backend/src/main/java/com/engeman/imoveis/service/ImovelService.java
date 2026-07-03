package com.engeman.imoveis.service;

import com.engeman.imoveis.dto.imovel.CreateImovelRequest;
import com.engeman.imoveis.dto.imovel.ImovelResponse;
import com.engeman.imoveis.dto.imovel.PageResponse;
import com.engeman.imoveis.dto.imovel.UpdateImovelRequest;
import com.engeman.imoveis.entity.Imovel;
import com.engeman.imoveis.entity.User;
import com.engeman.imoveis.enums.Role;
import com.engeman.imoveis.enums.TipoImovel;
import com.engeman.imoveis.exception.ResourceNotFoundException;
import com.engeman.imoveis.repository.ImovelRepository;
import com.engeman.imoveis.specification.ImovelSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ImovelService {

    private final ImovelRepository imovelRepository;

    public ImovelResponse create(CreateImovelRequest request, User corretor) {
        Imovel imovel = Imovel.builder()
                .titulo(request.getTitulo())
                .descricao(request.getDescricao())
                .tipo(request.getTipo())
                .preco(request.getPreco())
                .quartos(request.getQuartos())
                .banheiros(request.getBanheiros())
                .areaM2(request.getAreaM2())
                .endereco(request.getEndereco())
                .cidade(request.getCidade())
                .estado(request.getEstado())
                .corretor(corretor)
                .build();

        return new ImovelResponse(imovelRepository.save(imovel));
    }

    public List<ImovelResponse> listAll() {
        List<Imovel> imoveis = imovelRepository.findAll();
        return imoveis.stream()
                .map(ImovelResponse::new)
                .toList();
    }
        public List<ImovelResponse> listGerenciados(User user) {
        List<Imovel> imoveis = user.getRole() == Role.CORRETOR
                ? imovelRepository.findByCorretorId(user.getId())
                : imovelRepository.findAll();

        return imoveis.stream()
                .map(ImovelResponse::new)
                .toList();
        
    }

    public ImovelResponse findById(Long id) {
        return new ImovelResponse(getImovelOrThrow(id));
    }

    public ImovelResponse update(Long id, UpdateImovelRequest request, User user) {
        Imovel imovel = getImovelOrThrow(id);
        verificarPropriedade(imovel, user);

        imovel.setTitulo(request.getTitulo());
        imovel.setDescricao(request.getDescricao());
        imovel.setTipo(request.getTipo());
        imovel.setPreco(request.getPreco());
        imovel.setQuartos(request.getQuartos());
        imovel.setBanheiros(request.getBanheiros());
        imovel.setAreaM2(request.getAreaM2());
        imovel.setEndereco(request.getEndereco());
        imovel.setCidade(request.getCidade());
        imovel.setEstado(request.getEstado());

        return new ImovelResponse(imovelRepository.save(imovel));
    }

    public ImovelResponse updateStatus(Long id, boolean ativo, User user) {
        Imovel imovel = getImovelOrThrow(id);
        verificarPropriedade(imovel, user);
        imovel.setAtivo(ativo);
        return new ImovelResponse(imovelRepository.save(imovel));
    }

    public PageResponse<ImovelResponse> listPaginado(
            String titulo,
            TipoImovel tipo,
            BigDecimal precoMin,
            BigDecimal precoMax,
            Integer quartosMin,
            int page,
            int size,
            String sortBy,
            String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Specification<Imovel> spec = Specification
                .where(ImovelSpecification.apenasAtivos())
                .and(ImovelSpecification.tituloContains(titulo))
                .and(ImovelSpecification.tipoEquals(tipo))
                .and(ImovelSpecification.precoMinimo(precoMin))
                .and(ImovelSpecification.precoMaximo(precoMax))
                .and(ImovelSpecification.quartosMinimos(quartosMin));

        Page<ImovelResponse> resultado = imovelRepository.findAll(spec, pageable)
                .map(ImovelResponse::new);

        return new PageResponse<>(resultado);
    }

    private void verificarPropriedade(Imovel imovel, User user) {
        if (user.getRole() == Role.CORRETOR &&
                !imovel.getCorretor().getId().equals(user.getId())) {
            throw new AccessDeniedException("Você não tem permissão para editar este imóvel");
        }
    }

    public Imovel getImovelOrThrow(Long id) {
        return imovelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Imóvel", "id", id));
    }
}