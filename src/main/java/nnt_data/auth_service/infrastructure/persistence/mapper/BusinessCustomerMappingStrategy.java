package nnt_data.auth_service.infrastructure.persistence.mapper;

import nnt_data.auth_service.entity.BusinessCustomerRequest;
import nnt_data.auth_service.entity.RegisterRequest;
import nnt_data.auth_service.infrastructure.persistence.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class BusinessCustomerMappingStrategy extends BaseAuthMappingStrategy<BusinessCustomerRequest>{

    public BusinessCustomerMappingStrategy() {
        super(RegisterRequest.TypeEnum.BUSINESS, BusinessCustomerRequest.class);
    }

    @Override
    protected void mapSpecificFields(BusinessCustomerRequest source, UserEntity target) {
        target.setRuc(source.getRuc());
    }

    @Override
    protected void mapSpecificFields(UserEntity source, BusinessCustomerRequest target) {
        target.setRuc(source.getRuc());
    }
}
