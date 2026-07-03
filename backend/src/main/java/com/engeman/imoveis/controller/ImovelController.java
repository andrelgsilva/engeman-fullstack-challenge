package com.engeman.imoveis.controller;


import com.engeman.imoveis.dto.imovel.CreateImovelRequest;
import com.engeman.imoveis.dto.imovel.ImovelResponse;
import com.engeman.imoveis.dto.imovel.PageResponse;
import com.engeman.imoveis.dto.imovel.UpdateImovelRequest;
import com.engeman.imoveis.dto.imovel.UpdateImovelStatusRequest;
import com.engeman.imoveis.entity.User;
import com.engeman.imoveis.enums.TipoImovel;
import com.engeman.imoveis.service.ImovelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/imoveis")
@RequiredArgsConstructor
public class ImovelController {

    private final ImovelService imovelService;

    // ADMIN ou CORRETOR podem criar imóveis
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CORRETOR')")
    public ResponseEntity<ImovelResponse> create(
            @Valid @RequestBody CreateImovelRequest request,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(imovelService.create(request, user));
    }

    // Qualquer pessoa autenticada ou não pode listar (rota pública definida no SecurityConfig)
    @GetMapping
    public ResponseEntity<List<ImovelResponse>> listAll() {
        return ResponseEntity.ok(imovelService.listAll());
    }

    // Gestão: ADMIN vê tudo, CORRETOR vê apenas os próprios imóveis (ativos e inativos)
    @GetMapping("/gestao")
    @PreAuthorize("hasAnyRole('ADMIN', 'CORRETOR')")
    public ResponseEntity<List<ImovelResponse>> listarGestao(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(imovelService.listGerenciados(user));
    }

    // Qualquer pessoa pode ver detalhes
    @GetMapping("/{id}")
    public ResponseEntity<ImovelResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(imovelService.findById(id));
    }

    // ADMIN ou CORRETOR (dono) podem editar — regra fina vem na próxima task
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CORRETOR')")
    public ResponseEntity<ImovelResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateImovelRequest request,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(imovelService.update(id, request, user));
    }

    // ADMIN ou CORRETOR (dono) podem ativar/desativar
    // ADMIN ou CORRETOR (dono) podem ativar/desativar
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'CORRETOR')")
    public ResponseEntity<ImovelResponse> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateImovelStatusRequest request,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(imovelService.updateStatus(id, request.getAtivo(), user));
    }

    @GetMapping("/buscar")
    public ResponseEntity<PageResponse<ImovelResponse>> buscar(
            @RequestParam(required = false) String titulo,
            @RequestParam(required = false) TipoImovel tipo,
            @RequestParam(required = false) BigDecimal precoMin,
            @RequestParam(required = false) BigDecimal precoMax,
            @RequestParam(required = false) Integer quartosMin,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        return ResponseEntity.ok(
                imovelService.listPaginado(titulo, tipo, precoMin, precoMax, quartosMin, page, size, sortBy, sortDir));
    }
}

