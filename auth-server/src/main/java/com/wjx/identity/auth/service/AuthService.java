package com.wjx.identity.auth.service;

import com.wjx.identity.auth.dto.LoginReponse;
import com.wjx.identity.auth.dto.LoginRequest;
import com.wjx.identity.auth.dto.RegisterRequest;
import com.wjx.identity.common.exception.BusinessException;
import com.wjx.identity.security.jwt.JwtService;
import com.wjx.identity.user.entity.UserEntity;
import com.wjx.identity.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public void register(RegisterRequest request) {
        String username = request.username();
        Optional<UserEntity> existUser = userRepository.findByUsername(username);
        if (existUser.isPresent()) {
            throw new RuntimeException("用户已存在");
        }
        UserEntity user = new UserEntity();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setEmail(request.email());
        user.setEnabled(true);
        user.setCreatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    public LoginReponse login(LoginRequest request) {
        String username = request.username();
        Optional<UserEntity> existUser = userRepository.findByUsername(username);
        if (existUser.isEmpty()) {
            throw new BusinessException("用户名或密码错误");
        }
        UserEntity user = existUser.get();
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        String accessToken =
                jwtService.generateAccessToken(user);

        String refreshToken =
                jwtService.generateRefreshToken(user);
        return new LoginReponse(
                accessToken,
                refreshToken
        );
    }
}
