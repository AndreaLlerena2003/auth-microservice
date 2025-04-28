package nnt_data.auth_service.infrastructure.persistence.mapper;

import lombok.RequiredArgsConstructor;
import nnt_data.auth_service.entity.RegisterRequest;
import nnt_data.auth_service.infrastructure.persistence.entity.UserEntity;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class AuthMapperImpl implements AuthMapper{

    private final Map<RegisterRequest.TypeEnum, AuthMappingStrategy> strategyMap;

    public AuthMapperImpl(List<AuthMappingStrategy> strategies) {
        this.strategyMap = strategies.stream()
                .collect(Collectors.toMap(
                        strategy -> strategy.supports(RegisterRequest.TypeEnum.PERSONAL)
                                ? RegisterRequest.TypeEnum.PERSONAL
                                : RegisterRequest.TypeEnum.BUSINESS,
                        Function.identity()
                ));
    }

    @Override
    public Mono<UserEntity> toEntity(RegisterRequest user) {
        return Mono.justOrEmpty(user)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Cannot map null customer")))
                .map(c -> strategyMap.get(c.getType()).toEntity(c));
    }

    @Override
    public Mono<RegisterRequest> toDomain(UserEntity userEntity) {
        return Mono.justOrEmpty(userEntity)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Cannot map null customer")))
                .map(entity -> strategyMap.get(entity.getType()).toDomain(entity));
    }
}
