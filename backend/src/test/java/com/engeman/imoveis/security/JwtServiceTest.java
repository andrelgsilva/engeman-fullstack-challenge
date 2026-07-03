package com.engeman.imoveis.security;

import com.engeman.imoveis.entity.User;
import com.engeman.imoveis.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    // Chave base64 de 256 bits só para teste — não é (e não deve ser) a chave real usada em produção
    private static final String TEST_SECRET = "h72abyWpgEmVt+DNptE8dJ72xyLnc0nv3Bs1eVeJ/6A=";

    private JwtService jwtService;
    private User user;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secret", TEST_SECRET);
        ReflectionTestUtils.setField(jwtService, "expiration", 3600000L); // 1 hora

        user = User.builder()
                .id(1L)
                .name("Usuário Teste")
                .email("usuario@teste.com")
                .password("hash-fake")
                .role(Role.CLIENTE)
                .build();
    }

    @Test
    void generateToken_deveGerarTokenNaoNuloENaoVazio() {
        String token = jwtService.generateToken(user);

        assertThat(token).isNotNull();
        assertThat(token).isNotBlank();
        // Um JWT válido tem 3 partes separadas por ponto: header.payload.signature
        assertThat(token.split("\\.")).hasSize(3);
    }

    @Test
    void extractUsername_deveRetornarEmailCorreto() {
        String token = jwtService.generateToken(user);

        String username = jwtService.extractUsername(token);

        assertThat(username).isEqualTo("usuario@teste.com");
    }

    @Test
    void isTokenValid_deveRetornarTrueParaTokenDoUsuarioCorreto() {
        String token = jwtService.generateToken(user);

        boolean valido = jwtService.isTokenValid(token, user);

        assertThat(valido).isTrue();
    }

    @Test
    void isTokenValid_deveRetornarFalseParaUsuarioDiferente() {
        String token = jwtService.generateToken(user);

        User outroUsuario = User.builder()
                .id(2L)
                .name("Outro Usuário")
                .email("outro@teste.com")
                .password("hash-fake")
                .role(Role.CLIENTE)
                .build();

        boolean valido = jwtService.isTokenValid(token, outroUsuario);

        assertThat(valido).isFalse();
    }

    @Test
    void isTokenValid_deveRetornarFalseParaTokenExpirado() throws InterruptedException {
        ReflectionTestUtils.setField(jwtService, "expiration", 1L); // expira em 1ms
        String token = jwtService.generateToken(user);

        Thread.sleep(50); // garante que o token já expirou

        // A própria validação de expiração lança exceção ao tentar extrair claims de um token expirado
        org.junit.jupiter.api.Assertions.assertThrows(
                io.jsonwebtoken.ExpiredJwtException.class,
                () -> jwtService.isTokenValid(token, user)
        );
    }
}