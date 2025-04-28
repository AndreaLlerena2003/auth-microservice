package nnt_data.auth_service.domain.service;

import lombok.RequiredArgsConstructor;
import nnt_data.auth_service.entity.LoginRequest;
import nnt_data.auth_service.entity.LoginResponse;
import nnt_data.auth_service.entity.RegisterRequest;
import nnt_data.auth_service.infrastructure.persistence.entity.UserEntity;
import nnt_data.auth_service.infrastructure.persistence.mapper.AuthMapper;
import nnt_data.auth_service.infrastructure.persistence.repository.AuthRepository;
import nnt_data.auth_service.infrastructure.persistence.security.JwtUtils;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;
    private final ReactiveAuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final AuthMapper authMapper;

    public Mono<Object> register(RegisterRequest request) {
        return authRepository.existsByEmail(request.getEmail())
                .flatMap(exists -> {
                    if (Boolean.TRUE.equals(exists)) {
                        return Mono.error(new RuntimeException("Email already in use"));
                    }

                    return authMapper.toEntity(request)
                            .map(userEntity -> {
                                userEntity.setPassword(passwordEncoder.encode(request.getPassword()));
                                return userEntity;
                            })
                            .flatMap(authRepository::save)
                            .map(savedUser -> {
                                Map<String, Object> response = new HashMap<>();
                                response.put("id", savedUser.getId());
                                response.put("email", savedUser.getEmail());
                                response.put("message", "User registered successfully");
                                return response;
                            });
                });
    }

    public Mono<LoginResponse> authenticate(LoginRequest request) {
        System.out.println("Attempting to authenticate user: " + request.getEmail());

        return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        ).flatMap(auth -> {
            System.out.println("Authentication successful");
            UserEntity user = (UserEntity) auth.getPrincipal();
            String token = jwtUtils.generateToken(user);

            LoginResponse response = new LoginResponse();
            response.setToken(token);

            return Mono.just(response);
        }).onErrorResume(e -> {
            System.err.println("Authentication error: " + e.getClass().getName() + ": " + e.getMessage());
            if (e instanceof BadCredentialsException) {
                return Mono.error(new BadCredentialsException("Invalid email or password"));
            }
            return Mono.error(new AuthenticationException("Authentication failed: " + e.getMessage()) {});
        });
    }

}
