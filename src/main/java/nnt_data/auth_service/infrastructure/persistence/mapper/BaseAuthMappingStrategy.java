package nnt_data.auth_service.infrastructure.persistence.mapper;

import lombok.AllArgsConstructor;
import nnt_data.auth_service.entity.RegisterRequest;
import nnt_data.auth_service.infrastructure.persistence.entity.UserEntity;

@AllArgsConstructor
public abstract class BaseAuthMappingStrategy<T extends RegisterRequest> implements  AuthMappingStrategy{

    private final RegisterRequest.TypeEnum supportedType;
    private final Class<T> customerClass;

    @Override
    public UserEntity toEntity(RegisterRequest user) {
        T typedCustomer = customerClass.cast(user);
        UserEntity entity = new UserEntity();
        mapCommonFields(user, entity);
        entity.setType(supportedType);
        mapSpecificFields(typedCustomer, entity);
        return entity;
    }

    @Override
    public RegisterRequest toDomain(UserEntity entity) {
        T customer;
        try {
            customer = customerClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("No se pudo crear una instancia de " + customerClass.getSimpleName(), e);
        }
        mapCommonFields(entity, customer);
        customer.setType(supportedType);
        mapSpecificFields(entity, customer);
        return customer;
    }

    @Override
    public boolean supports(RegisterRequest.TypeEnum type) {
        return supportedType.equals(type);
    }

    protected void mapCommonFields(RegisterRequest source, UserEntity target) {
        target.setId(source.getId());
        target.setName(source.getName());
        target.setEmail(source.getEmail());
        target.setPhone(source.getPhone());
        target.setAddress(source.getAddress());
        target.setSubtype(source.getSubtype());
        target.setPassword(source.getPassword());
    }

    protected void mapCommonFields(UserEntity source, RegisterRequest target) {
        target.setId(source.getId());
        target.setName(source.getName());
        target.setEmail(source.getEmail());
        target.setPhone(source.getPhone());
        target.setAddress(source.getAddress());
        target.setSubtype(source.getSubtype());
        target.setPassword(source.getPassword());
    }

    protected abstract void mapSpecificFields(T source, UserEntity target);
    protected abstract void mapSpecificFields(UserEntity source, T target);
}
