package nnt_data.auth_service.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String TIMESTAMP = "timestamp";

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<Object>> handleGenericException(Exception ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("error", "Internal Server Error");
        body.put("message", ex.getMessage()); // Incluye el mensaje de la excepción
        // Opcional: detalles del stack trace

        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errors);
    }


    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleWebExchangeBindException(WebExchangeBindException ex) {
        Map<String, Object> response = new HashMap<>();
        return getThrowableMonoFunction(response).apply(ex);
    }


    @ExceptionHandler(IllegalArgumentException.class)
    public Mono<ResponseEntity<Object>> handleIllegalArgumentException(IllegalArgumentException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Bad Request");
        body.put("message", ex.getMessage());

        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public Mono<ResponseEntity<Object>> handleBadCredentialsException(BadCredentialsException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", HttpStatus.UNAUTHORIZED.value());
        body.put("error", "Unauthorized");
        body.put("message", "Invalid email or password");

        return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body));
    }

    @ExceptionHandler(AuthenticationException.class)
    public Mono<ResponseEntity<Object>> handleAuthenticationException(AuthenticationException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", HttpStatus.UNAUTHORIZED.value());
        body.put("error", "Unauthorized");
        body.put("message", ex.getMessage());

        return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body));
    }

    private static Function<Throwable, Mono<ResponseEntity<Map<String, Object>>>> getThrowableMonoFunction(Map<String, Object> response) {
        return t -> Mono.just(t).cast(WebExchangeBindException.class)
                .flatMap(e -> Mono.just(e.getFieldErrors()))
                .flatMapMany(Flux::fromIterable)
                .map(fieldError -> "Campo " + fieldError.getField() + " " + fieldError.getDefaultMessage())
                .collectList()
                .flatMap(l -> {
                    response.put(TIMESTAMP, new Date());
                    response.put("status", HttpStatus.BAD_REQUEST.value());
                    response.put("errors", l);
                    return Mono.just(ResponseEntity.badRequest().body(response));
                });
    }



}
