package com.engeman.imoveis.service;

import com.engeman.imoveis.dto.imovel.CreateImovelRequest;
import com.engeman.imoveis.dto.imovel.ImovelResponse;
import com.engeman.imoveis.dto.imovel.UpdateImovelRequest;
import com.engeman.imoveis.entity.Imovel;
import com.engeman.imoveis.entity.User;
import com.engeman.imoveis.enums.Role;
import com.engeman.imoveis.enums.TipoImovel;
import com.engeman.imoveis.exception.ResourceNotFoundException;
import com.engeman.imoveis.repository.ImovelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImovelServiceTest {

    @Mock
    private ImovelRepository imovelRepository;

    @InjectMocks
    private ImovelService imovelService;

    private User admin;
    private User corretorDono;
    private User corretorOutro;
    private Imovel imovelDoCorretor;

    @BeforeEach
    void setUp() {
        admin = User.builder().id(1L).name("Admin").email("admin@teste.com").role(Role.ADMIN).build();
        corretorDono = User.builder().id(2L).name("Corretor Dono").email("dono@teste.com").role(Role.CORRETOR).build();
        corretorOutro = User.builder().id(3L).name("Outro Corretor").email("outro@teste.com").role(Role.CORRETOR).build();

        imovelDoCorretor = Imovel.builder()
                .id(10L)
                .titulo("Casa Original")
                .descricao("Descrição original")
                .tipo(TipoImovel.CASA)
                .preco(new BigDecimal("300000"))
                .quartos(3)
                .banheiros(2)
                .areaM2(new BigDecimal("120"))
                .endereco("Rua A")
                .cidade("Recife")
                .estado("PE")
                .ativo(true)
                .corretor(corretorDono)
                .build();
    }

    // ---------- create ----------

    @Test
    void create_devePersistirImovelComCorretorInformado() {
        CreateImovelRequest request = new CreateImovelRequest();
        request.setTitulo("Casa Nova");
        request.setDescricao("Descrição");
        request.setTipo(TipoImovel.CASA);
        request.setPreco(new BigDecimal("250000"));
        request.setQuartos(2);
        request.setBanheiros(1);
        request.setAreaM2(new BigDecimal("80"));
        request.setEndereco("Rua B");
        request.setCidade("Recife");
        request.setEstado("PE");

        when(imovelRepository.save(any(Imovel.class))).thenAnswer(inv -> inv.getArgument(0));

        ImovelResponse response = imovelService.create(request, corretorDono);

        assertThat(response.getTitulo()).isEqualTo("Casa Nova");
        verify(imovelRepository).save(argThat(i -> i.getCorretor().equals(corretorDono)));
    }

    // ---------- listGerenciados (regra de autorização na listagem) ----------

    @Test
    void listGerenciados_corretorDeveVerApenasOsProprios() {
        when(imovelRepository.findByCorretorId(corretorDono.getId())).thenReturn(List.of(imovelDoCorretor));

        List<ImovelResponse> resultado = imovelService.listGerenciados(corretorDono);

        assertThat(resultado).hasSize(1);
        verify(imovelRepository).findByCorretorId(corretorDono.getId());
        verify(imovelRepository, never()).findAll();
    }

    @Test
    void listGerenciados_adminDeveVerTodos() {
        when(imovelRepository.findAll()).thenReturn(List.of(imovelDoCorretor));

        List<ImovelResponse> resultado = imovelService.listGerenciados(admin);

        assertThat(resultado).hasSize(1);
        verify(imovelRepository).findAll();
        verify(imovelRepository, never()).findByCorretorId(any());
    }

    // ---------- findById ----------

    @Test
    void findById_deveRetornarImovelQuandoExiste() {
        when(imovelRepository.findById(10L)).thenReturn(Optional.of(imovelDoCorretor));

        ImovelResponse response = imovelService.findById(10L);

        assertThat(response.getTitulo()).isEqualTo("Casa Original");
    }

    @Test
    void findById_deveLancarExcecaoQuandoNaoExiste() {
        when(imovelRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> imovelService.findById(999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ---------- update: regra de autorização ----------

    @Test
    void update_donoDeveConseguirEditarProprioImovel() {
        when(imovelRepository.findById(10L)).thenReturn(Optional.of(imovelDoCorretor));
        when(imovelRepository.save(any(Imovel.class))).thenAnswer(inv -> inv.getArgument(0));

        UpdateImovelRequest request = new UpdateImovelRequest();
        request.setTitulo("Casa Editada");
        request.setDescricao("Nova descrição");
        request.setTipo(TipoImovel.CASA);
        request.setPreco(new BigDecimal("320000"));
        request.setQuartos(3);
        request.setBanheiros(2);
        request.setAreaM2(new BigDecimal("120"));
        request.setEndereco("Rua A");
        request.setCidade("Recife");
        request.setEstado("PE");

        ImovelResponse response = imovelService.update(10L, request, corretorDono);

        assertThat(response.getTitulo()).isEqualTo("Casa Editada");
    }

    @Test
    void update_adminDeveConseguirEditarImovelDeQualquerCorretor() {
        when(imovelRepository.findById(10L)).thenReturn(Optional.of(imovelDoCorretor));
        when(imovelRepository.save(any(Imovel.class))).thenAnswer(inv -> inv.getArgument(0));

        UpdateImovelRequest request = new UpdateImovelRequest();
        request.setTitulo("Editado pelo Admin");
        request.setDescricao("Nova descrição");
        request.setTipo(TipoImovel.CASA);
        request.setPreco(new BigDecimal("320000"));
        request.setQuartos(3);
        request.setBanheiros(2);
        request.setAreaM2(new BigDecimal("120"));
        request.setEndereco("Rua A");
        request.setCidade("Recife");
        request.setEstado("PE");

        ImovelResponse response = imovelService.update(10L, request, admin);

        assertThat(response.getTitulo()).isEqualTo("Editado pelo Admin");
    }

    @Test
    void update_corretorNaoDonoDeveReceberAccessDenied() {
        when(imovelRepository.findById(10L)).thenReturn(Optional.of(imovelDoCorretor));

        UpdateImovelRequest request = new UpdateImovelRequest();
        request.setTitulo("Tentativa de invasão");

        assertThatThrownBy(() -> imovelService.update(10L, request, corretorOutro))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("Você não tem permissão para editar este imóvel");

        verify(imovelRepository, never()).save(any());
    }

    // ---------- updateStatus: regra de autorização ----------

    @Test
    void updateStatus_donoDeveConseguirAtivarDesativar() {
        when(imovelRepository.findById(10L)).thenReturn(Optional.of(imovelDoCorretor));
        when(imovelRepository.save(any(Imovel.class))).thenAnswer(inv -> inv.getArgument(0));

        ImovelResponse response = imovelService.updateStatus(10L, false, corretorDono);

        assertThat(response.isAtivo()).isFalse();
    }

    @Test
    void updateStatus_corretorNaoDonoDeveReceberAccessDenied() {
        when(imovelRepository.findById(10L)).thenReturn(Optional.of(imovelDoCorretor));

        assertThatThrownBy(() -> imovelService.updateStatus(10L, false, corretorOutro))
                .isInstanceOf(AccessDeniedException.class);

        verify(imovelRepository, never()).save(any());
    }

    @Test
    void updateStatus_deveLancarExcecaoQuandoImovelNaoExiste() {
        when(imovelRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> imovelService.updateStatus(999L, false, admin))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ---------- listPaginado ----------

    @SuppressWarnings("unchecked")
    @Test
    void listPaginado_deveRetornarPaginaDeResultados() {
        Page<Imovel> page = new PageImpl<>(List.of(imovelDoCorretor));
        when(imovelRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        var resultado = imovelService.listPaginado(
                null, null, null, null, null, 0, 10, "createdAt", "desc"
        );

        assertThat(resultado.getContent()).hasSize(1);
        verify(imovelRepository).findAll(any(Specification.class), any(Pageable.class));
    }
}