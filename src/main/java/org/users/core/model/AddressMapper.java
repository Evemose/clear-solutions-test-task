package org.users.core.model;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.users.core.model.dto.GetAddressDTO;
import org.users.core.model.dto.PostAddressDTO;
import org.users.core.model.entities.Address;

@Mapper(componentModel = "spring")
public interface AddressMapper {

    Address toEntity(PostAddressDTO address);

    GetAddressDTO toDTO(Address address);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Address toEntity(PostAddressDTO address, @MappingTarget Address target);
}
