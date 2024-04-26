package org.users.core.model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.users.core.model.dto.GetAddressDTO;
import org.users.core.model.dto.PostAddressDTO;
import org.users.core.model.entities.Address;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = AddressMapperImpl.class)
public class AddressMapperTest {

    static PostAddressDTO postAddressDTO;
    static Address address;
    @Autowired
    private AddressMapper addressMapper;

    @BeforeAll
    public static void setup() {
        postAddressDTO = new PostAddressDTO(123, "Main St", "Springfield", "USA", "33000");
        address = new Address(123, "Main St", "Springfield", "USA", "33000");
    }

    @Test
    public void testToEntity() {
        var result = addressMapper.toEntity(postAddressDTO);
        assertEquals(address, result);
    }

    @Test
    public void testToDTO() {
        GetAddressDTO result = addressMapper.toDTO(address);
        assertEquals(new GetAddressDTO(123, "Main St", "Springfield", "USA", "33000"), result);
    }

    @Test
    public void testToEntityWithTarget() {
        var target = new Address(456, "Second St", "Springfield", "USA", "33000");
        var post = postAddressDTO.withCity(null)
                .withCountry(null);
        var result = addressMapper.toEntity(post, address);
        assertEquals(target.withHouseNumber(123).withStreet("Main St").withZipCode("33000"), result);
    }
}
