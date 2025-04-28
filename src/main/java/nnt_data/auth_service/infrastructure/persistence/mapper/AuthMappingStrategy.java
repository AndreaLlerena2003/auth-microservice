package nnt_data.auth_service.infrastructure.persistence.mapper;

import nnt_data.auth_service.entity.RegisterRequest;
import nnt_data.auth_service.infrastructure.persistence.entity.UserEntity;

public interface AuthMappingStrategy {
    UserEntity toEntity(RegisterRequest user);
    RegisterRequest toDomain(UserEntity entity);
    boolean supports(RegisterRequest.TypeEnum type);
}
