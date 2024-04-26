package org.users.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.users.core.model.dto.GetUserDTO;
import org.users.core.model.dto.PostAddressDTO;
import org.users.core.model.dto.PostUserDTO;
import org.users.core.model.entities.User;
import org.users.core.utils.CaseAndExplanation;

import java.time.LocalDate;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    UserService userService;

    @Value("${adult.age}")
    int adultAge;

    // existing user to assert expected results
    User user;

    @PostConstruct
    public void init() {
        user = userService.findById(1L).orElseThrow();
    }


    public Stream<CaseAndExplanation<PostUserDTO>> invalidUserProvider() {
        return Stream.of(
                new CaseAndExplanation<>(
                        new PostUserDTO("valid@gmail.com", "John", "Doe", LocalDate.now(), null, null),
                        "birthDate: must be at least %d years ago".formatted(this.adultAge)
                ),
                new CaseAndExplanation<>(
                        new PostUserDTO("valid", "John", "Doe", LocalDate.of(2000, 1, 1), null, null),
                        "email: must be a well-formed email address"
                ),
                new CaseAndExplanation<>(
                        new PostUserDTO("valid@gmail.com", "   ", "Doe", LocalDate.of(2000, 1, 1), null, null),
                        "firstName: must not be blank"
                ),
                new CaseAndExplanation<>(
                        new PostUserDTO("valid@gmail.com", "John", "", LocalDate.of(2000, 1, 1), null, null),
                        "lastName: must not be blank"
                ),
                new CaseAndExplanation<>(
                        new PostUserDTO(null, "John", "Doe", LocalDate.of(2000, 1, 1), null, null),
                        "email: must not be null"
                ),
                new CaseAndExplanation<>(
                        new PostUserDTO("valid@gmail.com", "John", "Doe", LocalDate.of(2000, 1, 1), new PostAddressDTO(12, "Str", "City", "Cntry", "4123"), null),
                        "address.zipCode: must match \\\"\\\\d{5}\\\""
                ),
                new CaseAndExplanation<>(
                        new PostUserDTO(this.user.getEmail(), this.user.getFirstName(), this.user.getLastName(), this.user.getBirthDate(), null, null),
                        "Email must be unique, but a duplicate was found: '%s'".formatted(this.user.getEmail())
                )
        );
    }

    @Test
    public void testGet_Valid() throws Exception {
        var result = this.mockMvc.perform(get("/users/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
        var user = objectMapper.readValue(result.getResponse().getContentAsString(), User.class);
        assertThat(user)
                .usingRecursiveComparison()
                .ignoringFields("createdAt", "updatedAt")
                .isEqualTo(this.user);
    }

    @Test
    public void testGet_Invalid() throws Exception {
        this.mockMvc.perform(get("/users/{id}", 123L)).
                andExpect(status().isNotFound());
    }

    @Test
    @DirtiesContext
    public void testSave_Valid() throws Exception {
        var newUser = objectMapper.convertValue(user, PostUserDTO.class)
                .withEmail("mock@gmail.com")
                .withPhoneNumber("123-456-7890");

        var result = this.mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(newUser))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                ).andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
        var user = objectMapper.readValue(result.getResponse().getContentAsString(), GetUserDTO.class);

        assertThat(user)
                .usingRecursiveComparison()
                .ignoringFields("updatedAt")
                .isEqualTo(objectMapper.convertValue(user, GetUserDTO.class));
    }

    @ParameterizedTest
    @MethodSource("invalidUserProvider")
    @DirtiesContext
    public void testSave_Invalid(CaseAndExplanation<PostUserDTO> caseAndExplanation) throws Exception {
        this.mockMvc.perform(post("/users")
                .content(objectMapper.writeValueAsString(caseAndExplanation.input()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        ).andExpectAll(
                status().isBadRequest(),
                content().contentType(MediaType.APPLICATION_JSON_VALUE),
                content().string("[\"" + caseAndExplanation.explanation() + "\"]")
        );
    }

    @Test
    @DirtiesContext
    public void testPut_Valid() throws Exception {
        var updatedUser = objectMapper.convertValue(user, PostUserDTO.class)
                .withFirstName("Jane")
                .withLastName("Doe")
                .withEmail("mock@gmail.com")
                .withPhoneNumber(null);

        this.mockMvc.perform(put("/users/{id}", 1L)
                        .content(objectMapper.writeValueAsString(updatedUser))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                ).andExpect(status().isNoContent());
    }

    @ParameterizedTest
    @MethodSource("invalidUserProvider")
    @DirtiesContext
    public void testPut_Invalid(CaseAndExplanation<PostUserDTO> caseAndExplanation) throws Exception {
        this.mockMvc.perform(put("/users/{id}", 25L)
                .content(objectMapper.writeValueAsString(caseAndExplanation.input()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        ).andExpectAll(
                status().isBadRequest(),
                content().contentType(MediaType.APPLICATION_JSON_VALUE),
                content().string("[\"" + caseAndExplanation.explanation() + "\"]")
        );
    }
}
