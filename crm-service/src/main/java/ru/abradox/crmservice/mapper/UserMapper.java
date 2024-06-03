package ru.abradox.crmservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.abradox.crmservice.entity.UserEntity;
import ru.abradox.platformapi.crm.ProviderUserInfo;
import ru.abradox.platformapi.crm.UserInfo;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {

    UserInfo mapUserEntityToUserInfo(UserEntity userEntity);

    UserEntity mapProviderUserInfoToUserEntity(ProviderUserInfo providerUserInfo);
}
