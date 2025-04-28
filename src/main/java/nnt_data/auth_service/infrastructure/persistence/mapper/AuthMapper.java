package nnt_data.auth_service.infrastructure.persistence.mapper;

import nnt_data.auth_service.entity.RegisterRequest;
import nnt_data.auth_service.infrastructure.persistence.entity.UserEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public interface AuthMapper {
    Mono<UserEntity> toEntity(RegisterRequest user);
    Mono<RegisterRequest> toDomain(UserEntity userEntity);
}
