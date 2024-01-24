package ru.abradox.crmservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.abradox.common.ProviderUserInfo;
import ru.abradox.common.UserInfo;
import ru.abradox.crmservice.entity.UserEntity;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {

    UserInfo mapUserEntityToUserInfo(UserEntity userEntity);

    UserEntity mapProviderUserInfoToUserEntity(ProviderUserInfo providerUserInfo);
}
