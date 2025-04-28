package nnt_data.auth_service.domain.service;

import lombok.RequiredArgsConstructor;
import nnt_data.auth_service.infrastructure.persistence.repository.AuthRepository;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements ReactiveUserDetailsService {

    private final AuthRepository authRepository;

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return authRepository.findByEmail(username)
                .cast(UserDetails.class);
    }

}
