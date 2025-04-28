package nnt_data.auth_service.infrastructure.persistence.security;

import lombok.RequiredArgsConstructor;
import nnt_data.auth_service.domain.service.UserDetailsServiceImpl;
import nnt_data.auth_service.infrastructure.persistence.entity.UserEntity;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationManager implements ReactiveAuthenticationManager {
    private final UserDetailsServiceImpl userDetailsService;
    private final JwtUtils jwtUtils;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String authToken = authentication.getCredentials().toString();
        String username = jwtUtils.extractUsername(authToken);

        return userDetailsService.findByUsername(username)
                .filter(userDetails -> jwtUtils.validateToken(authToken, (UserEntity) userDetails))
                .map(userDetails -> {
                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );
                    SecurityContextHolder.getContext().setAuthentication(auth);
                    return auth;
                });
    }
}
