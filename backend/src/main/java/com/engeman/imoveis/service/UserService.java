package com.engeman.imoveis.service;

import com.engeman.imoveis.dto.user.CreateUserRequest;
import com.engeman.imoveis.dto.user.UserResponse;
import com.engeman.imoveis.entity.User;
import com.engeman.imoveis.exception.BusinessException;
import com.engeman.imoveis.exception.ResourceNotFoundException;
import com.engeman.imoveis.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserResponse createUser(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("E-mail já cadastrado");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();

        return new UserResponse(userRepository.save(user));
    }

    public List<UserResponse> listAll() {
        return userRepository.findAll()
                .stream()
                .map(UserResponse::new)
                .toList();
    }

    public UserResponse findById(Long id) {
        return userRepository.findById(id)
                .map(UserResponse::new)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "id", id));
    }

    public void deactivate(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "id", id));
        user.setActive(false);
        userRepository.save(user);
    }
}