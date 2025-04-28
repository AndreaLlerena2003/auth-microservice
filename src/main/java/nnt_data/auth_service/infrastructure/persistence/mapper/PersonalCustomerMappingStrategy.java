package nnt_data.auth_service.infrastructure.persistence.mapper;

import nnt_data.auth_service.entity.PersonalCustomerRequest;
import nnt_data.auth_service.entity.RegisterRequest;
import nnt_data.auth_service.infrastructure.persistence.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class PersonalCustomerMappingStrategy extends BaseAuthMappingStrategy<PersonalCustomerRequest>{

    public PersonalCustomerMappingStrategy() {
        super(RegisterRequest.TypeEnum.PERSONAL, PersonalCustomerRequest.class);
    }


    @Override
    protected void mapSpecificFields(PersonalCustomerRequest source, UserEntity target) {
        target.setDni(source.getDni());
    }

    @Override
    protected void mapSpecificFields(UserEntity source, PersonalCustomerRequest target) {
        target.setDni(source.getDni());
    }
}
