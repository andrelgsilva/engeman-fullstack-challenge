package com.engeman.imoveis.controller;

import com.engeman.imoveis.dto.favorito.FavoritoResponse;
import com.engeman.imoveis.entity.User;
import com.engeman.imoveis.service.FavoritoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favoritos")
@RequiredArgsConstructor
public class FavoritoController {

    private final FavoritoService favoritoService;

    @PostMapping("/{imovelId}")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<FavoritoResponse> adicionar(
            @PathVariable Long imovelId,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(favoritoService.adicionar(imovelId, user));
    }

    @DeleteMapping("/{imovelId}")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<Void> remover(
            @PathVariable Long imovelId,
            @AuthenticationPrincipal User user) {
        favoritoService.remover(imovelId, user);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<List<FavoritoResponse>> listar(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(favoritoService.listar(user));
    }
}