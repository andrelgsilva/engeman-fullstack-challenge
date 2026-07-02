package com.engeman.imoveis.service;

import com.engeman.imoveis.dto.auth.AuthResponse;
import com.engeman.imoveis.dto.auth.LoginRequest;
import com.engeman.imoveis.dto.auth.RegisterRequest;
import com.engeman.imoveis.entity.User;
import com.engeman.imoveis.enums.Role;
import com.engeman.imoveis.exception.BusinessException;
import com.engeman.imoveis.repository.UserRepository;
import com.engeman.imoveis.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("E-mail já cadastrado");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.CLIENTE) // registro público sempre como CLIENTE
                .build();

        userRepository.save(user);

        String token = jwtService.generateToken(user);
        return new AuthResponse(token, user.getName(), user.getEmail(), user.getRole());
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException("Usuário não encontrado"));

        String token = jwtService.generateToken(user);
        return new AuthResponse(token, user.getName(), user.getEmail(), user.getRole());
    }
}