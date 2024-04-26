package org.users.core.model;

import lombok.Setter;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.users.core.model.dto.GetUserDTO;
import org.users.core.model.dto.PostUserDTO;
import org.users.core.model.entities.User;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class UserMapper {
    @Setter(onMethod_ = @Autowired)
    protected AddressMapper addressMapper;

    @Mapping(target = "address", expression = "java(addressMapper.toDTO(user.getAddress()))")
    public abstract GetUserDTO toDto(User user);

    @Mapping(target = "address", expression = "java(addressMapper.toEntity(userDto.address()))")
    public abstract User toEntity(PostUserDTO userDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "address", expression = "java(addressMapper.toEntity(userDto.address(), user.getAddress()))")
    public abstract User toEntity(PostUserDTO userDto, @MappingTarget User user);
}
