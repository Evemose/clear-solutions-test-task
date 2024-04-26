package org.users.core.model;

import org.mapstruct.*;
import org.users.core.model.dto.GetAddressDTO;
import org.users.core.model.dto.PostAddressDTO;
import org.users.core.model.entities.Address;

import java.util.Objects;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AddressMapper {

    Address toEntity(PostAddressDTO address);

    GetAddressDTO toDTO(Address address);

    // had to implement manually cause mapstruct works wierd when mapping into record
    default Address toEntity(PostAddressDTO address, @MappingTarget Address target) {
        return new Address(
                Objects.requireNonNullElse(address.houseNumber(), target.houseNumber()),
                Objects.requireNonNullElse(address.street(), target.street()),
                Objects.requireNonNullElse(address.city(), target.city()),
                Objects.requireNonNullElse(address.country(), target.country()),
                Objects.requireNonNullElse(address.zipCode(), target.zipCode())
        );
    }
}
