package ru.abradox.tokenservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.abradox.client.token.TokenDto;
import ru.abradox.tokenservice.entity.TokenEntity;

import java.util.List;

@Mapper(componentModel = "Spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TokenMapper {

    TokenDto mapTokenEntityToTokenDto(TokenEntity tokenEntity);

    List<TokenDto> mapTokenEntityToTokenDto(List<TokenEntity> tokenEntity);

}
