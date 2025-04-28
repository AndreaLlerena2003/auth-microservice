package nnt_data.auth_service.controller;

import lombok.RequiredArgsConstructor;
import nnt_data.auth_service.api.ApiApi;
import nnt_data.auth_service.domain.service.AuthService;
import nnt_data.auth_service.entity.LoginRequest;
import nnt_data.auth_service.entity.LoginResponse;
import nnt_data.auth_service.entity.RegisterRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AuthController implements ApiApi {

    private final AuthService authService;

    /**
     * POST /api/auth/login : Login de usuario
     * Autenticación de usuario y generación de token JWT
     *
     * @param loginRequest (required)
     * @param exchange
     * @return Login exitoso (status code 200)
     * or Credenciales inválidas (status code 401)
     * or Error interno del servidor (status code 500)
     */
    @Override
    public Mono<ResponseEntity<LoginResponse>> login(Mono<LoginRequest> loginRequest, ServerWebExchange exchange) {
        return loginRequest
                .flatMap(authService::authenticate)
                .map(response -> ResponseEntity.ok(response))
                .onErrorResume(error -> Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()));
    }


    /**
     * POST /api/auth/register : Registro de cliente
     * Registro de un nuevo cliente (personal o empresarial)
     *
     * @param registerRequest (required)
     * @param exchange
     * @return Cliente registrado correctamente (status code 201)
     * or Datos de entrada incorrectos o usuario ya existente (status code 400)
     * or Error interno del servidor (status code 500)
     */
    @Override
    public Mono<ResponseEntity<Map<String, Object>>> register(Mono<RegisterRequest> registerRequest, ServerWebExchange exchange) {
        return registerRequest
                .flatMap(request -> authService.register(request))
                .map(response -> ResponseEntity.status(HttpStatus.CREATED).body((Map<String, Object>) response))
                .onErrorResume(error -> {
                    if (error.getMessage().contains("Email already in use")) {
                        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
                    }
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }


}
