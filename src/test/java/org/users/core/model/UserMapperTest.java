package org.users.core.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.users.core.DateTimeSerializationConfig;
import org.users.core.model.dto.GetAddressDTO;
import org.users.core.model.dto.GetUserDTO;
import org.users.core.model.dto.PostAddressDTO;
import org.users.core.model.dto.PostUserDTO;
import org.users.core.model.entities.Address;
import org.users.core.model.entities.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.MockitoAnnotations.openMocks;

@SpringBootTest(classes = {UserMapperImpl.class, AddressMapperImpl.class, ObjectMapper.class, DateTimeSerializationConfig.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserMapperTest {

    @Autowired
    UserMapper userMapper;

    @Autowired
    ObjectMapper objectMapper;

    PostUserDTO postUserDTO;

    User user;

    @BeforeEach
    public void init() {
        openMocks(this);
    }

    @BeforeAll
    public void setup() {
        postUserDTO = new PostUserDTO(
                "john.doe@example.com",
                "John",
                "Doe",
                LocalDate.now(),
                new PostAddressDTO(123, "Main St", "Springfield", "USA", "33000"),
                "555-555-5555"
        );
        user = new User("john.doe@example.com", "John", "Doe", LocalDate.now());
        user.setAddress(new Address(123, "Main St", "Springfield", "USA", "33000"));
        user.setPhoneNumber("555-555-5555");
    }

    // for some reason in tests objectMapper doesnt have modules registered by default
    @PostConstruct
    public void setupObjectMapper() {
        objectMapper.findAndRegisterModules();
    }

    @Test
    public void testToEntity() {
        var result = userMapper.toEntity(postUserDTO);
        assertEquals(user, result);
    }

    @Test
    public void testToDTO() {
        var result = userMapper.toDto(user);
        assertEquals(new GetUserDTO(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getBirthDate(),
                objectMapper.convertValue(user.getAddress(), GetAddressDTO.class),
                user.getPhoneNumber(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        ), result);
    }

    @Test
    public void testToEntityWithTarget() throws Exception {
        var newBirthDate = LocalDate.now();
        var post = postUserDTO
                .withEmail(null)
                .withBirthDate(newBirthDate);
        var result = userMapper.toEntity(post, user);
        var expected = objectMapper.readValue(objectMapper.writeValueAsString(user), User.class);
        expected.setBirthDate(newBirthDate);
        expected.setEmail(user.getEmail());
        assertEquals(expected, result);
    }
}