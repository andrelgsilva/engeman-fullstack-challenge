package com.engeman.imoveis.service;

import com.engeman.imoveis.dto.auth.AuthResponse;
import com.engeman.imoveis.dto.auth.LoginRequest;
import com.engeman.imoveis.dto.auth.RegisterRequest;
import com.engeman.imoveis.entity.User;
import com.engeman.imoveis.enums.Role;
import com.engeman.imoveis.exception.BusinessException;
import com.engeman.imoveis.repository.UserRepository;
import com.engeman.imoveis.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private User user;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setName("Cliente Teste");
        registerRequest.setEmail("cliente@teste.com");
        registerRequest.setPassword("senha123");

        loginRequest = new LoginRequest();
        loginRequest.setEmail("cliente@teste.com");
        loginRequest.setPassword("senha123");

        user = User.builder()
                .id(1L)
                .name("Cliente Teste")
                .email("cliente@teste.com")
                .password("hash-fake")
                .role(Role.CLIENTE)
                .build();
    }

    @Test
    void register_deveCriarUsuarioComRoleClienteERetornarToken() {
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("hash-fake");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(jwtService.generateToken(any(User.class))).thenReturn("token-fake");

        AuthResponse response = authService.register(registerRequest);

        assertThat(response.getToken()).isEqualTo("token-fake");
        assertThat(response.getEmail()).isEqualTo("cliente@teste.com");
        assertThat(response.getRole()).isEqualTo(Role.CLIENTE);

        // Registro público sempre cria como CLIENTE, nunca ADMIN/CORRETOR
        verify(userRepository).save(argThat(savedUser -> savedUser.getRole() == Role.CLIENTE));
    }

    @Test
    void register_deveLancarExcecaoQuandoEmailJaCadastrado() {
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessage("E-mail já cadastrado");

        verify(userRepository, never()).save(any());
        verify(jwtService, never()).generateToken(any());
    }

    @Test
    void login_deveAutenticarERetornarToken() {
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn("token-fake");

        AuthResponse response = authService.login(loginRequest);

        assertThat(response.getToken()).isEqualTo("token-fake");
        assertThat(response.getEmail()).isEqualTo("cliente@teste.com");
        verify(authenticationManager).authenticate(any());
    }

    @Test
    void login_deveLancarExcecaoQuandoAuthenticationManagerRejeitaCredenciais() {
        doThrow(new org.springframework.security.authentication.BadCredentialsException("Credenciais inválidas"))
                .when(authenticationManager).authenticate(any());

        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(org.springframework.security.authentication.BadCredentialsException.class);

        verify(userRepository, never()).findByEmail(anyString());
        verify(jwtService, never()).generateToken(any());
    }

    @Test
    void login_deveLancarExcecaoQuandoUsuarioNaoEncontradoAposAutenticar() {
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Usuário não encontrado");
    }
}