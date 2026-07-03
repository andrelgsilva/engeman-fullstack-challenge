package com.engeman.imoveis.service;

import com.engeman.imoveis.dto.favorito.FavoritoResponse;
import com.engeman.imoveis.entity.Favorito;
import com.engeman.imoveis.entity.Imovel;
import com.engeman.imoveis.entity.User;
import com.engeman.imoveis.enums.Role;
import com.engeman.imoveis.enums.TipoImovel;
import com.engeman.imoveis.exception.BusinessException;
import com.engeman.imoveis.exception.ResourceNotFoundException;
import com.engeman.imoveis.repository.FavoritoRepository;
import com.engeman.imoveis.repository.ImovelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FavoritoServiceTest {

    @Mock
    private FavoritoRepository favoritoRepository;

    @Mock
    private ImovelRepository imovelRepository;

    @InjectMocks
    private FavoritoService favoritoService;

    private User cliente;
    private Imovel imovelAtivo;
    private Imovel imovelInativo;

    @BeforeEach
    void setUp() {
        cliente = User.builder().id(1L).name("Cliente Teste").email("cliente@teste.com").role(Role.CLIENTE).build();

        imovelAtivo = Imovel.builder()
                .id(10L)
                .titulo("Casa Ativa")
                .tipo(TipoImovel.CASA)
                .preco(new BigDecimal("300000"))
                .quartos(3)
                .ativo(true)
                .build();

        imovelInativo = Imovel.builder()
                .id(20L)
                .titulo("Casa Inativa")
                .tipo(TipoImovel.CASA)
                .preco(new BigDecimal("300000"))
                .quartos(3)
                .ativo(false)
                .build();
    }

    // ---------- adicionar ----------

    @Test
    void adicionar_devePersistirFavoritoQuandoImovelAtivoENaoDuplicado() {
        when(imovelRepository.findById(10L)).thenReturn(Optional.of(imovelAtivo));
        when(favoritoRepository.existsByClienteAndImovel(cliente, imovelAtivo)).thenReturn(false);
        when(favoritoRepository.save(any(Favorito.class))).thenAnswer(inv -> inv.getArgument(0));

        FavoritoResponse response = favoritoService.adicionar(10L, cliente);

        assertThat(response).isNotNull();
        verify(favoritoRepository).save(argThat(f -> f.getCliente().equals(cliente) && f.getImovel().equals(imovelAtivo)));
    }

    @Test
    void adicionar_deveLancarExcecaoQuandoImovelNaoExiste() {
        when(imovelRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> favoritoService.adicionar(999L, cliente))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(favoritoRepository, never()).save(any());
    }

    @Test
    void adicionar_deveLancarExcecaoQuandoImovelInativo() {
        when(imovelRepository.findById(20L)).thenReturn(Optional.of(imovelInativo));

        assertThatThrownBy(() -> favoritoService.adicionar(20L, cliente))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Não é possível favoritar um imóvel inativo");

        verify(favoritoRepository, never()).save(any());
    }

    @Test
    void adicionar_deveLancarExcecaoQuandoJaFavoritado() {
        when(imovelRepository.findById(10L)).thenReturn(Optional.of(imovelAtivo));
        when(favoritoRepository.existsByClienteAndImovel(cliente, imovelAtivo)).thenReturn(true);

        assertThatThrownBy(() -> favoritoService.adicionar(10L, cliente))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Imóvel já está nos favoritos");

        verify(favoritoRepository, never()).save(any());
    }

    // ---------- remover ----------

    @Test
    void remover_deveExcluirFavoritoExistente() {
        Favorito favorito = Favorito.builder().id(1L).cliente(cliente).imovel(imovelAtivo).build();

        when(imovelRepository.findById(10L)).thenReturn(Optional.of(imovelAtivo));
        when(favoritoRepository.findByClienteAndImovel(cliente, imovelAtivo)).thenReturn(Optional.of(favorito));

        favoritoService.remover(10L, cliente);

        verify(favoritoRepository).delete(favorito);
    }

    @Test
    void remover_deveLancarExcecaoQuandoImovelNaoExiste() {
        when(imovelRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> favoritoService.remover(999L, cliente))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(favoritoRepository, never()).delete(any());
    }

    @Test
    void remover_deveLancarExcecaoQuandoFavoritoNaoExiste() {
        when(imovelRepository.findById(10L)).thenReturn(Optional.of(imovelAtivo));
        when(favoritoRepository.findByClienteAndImovel(cliente, imovelAtivo)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> favoritoService.remover(10L, cliente))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(favoritoRepository, never()).delete(any());
    }

    // ---------- listar ----------

    @Test
    void listar_deveRetornarFavoritosDoCliente() {
        Favorito favorito = Favorito.builder().id(1L).cliente(cliente).imovel(imovelAtivo).build();
        when(favoritoRepository.findByCliente(cliente)).thenReturn(List.of(favorito));

        List<FavoritoResponse> resultado = favoritoService.listar(cliente);

        assertThat(resultado).hasSize(1);
    }

    @Test
    void listar_deveRetornarListaVaziaQuandoClienteNaoTemFavoritos() {
        when(favoritoRepository.findByCliente(cliente)).thenReturn(List.of());

        List<FavoritoResponse> resultado = favoritoService.listar(cliente);

        assertThat(resultado).isEmpty();
    }
}