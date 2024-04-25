package org.users.core;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.users.core.model.UserMapper;
import org.users.core.model.dto.GetUserDTO;
import org.users.core.model.dto.PostUserDTO;
import org.users.core.validation.Post;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    final UserService userService;
    final UserMapper userMapper;

    @GetMapping("/{id}")
    public ResponseEntity<GetUserDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(
                userService.findById(id).map(userMapper::toDto).orElseThrow(
                        () -> new EntityNotFoundException("User with id " + id + " not found")
                )
        );
    }

    @GetMapping
    @ApiResponse(
            description = "Returns all users with a birthdate between the given dates",
            responseCode = "200"
    )
    public ResponseEntity<List<GetUserDTO>> getAll(@RequestParam(defaultValue = "1900-01-01") LocalDate minBirthdate,
                                                   @RequestParam(defaultValue = "9999-12-31") LocalDate maxBirthdate) {
        return ResponseEntity.ok(
                userService.findByBirthdateBetween(minBirthdate, maxBirthdate).stream().map(userMapper::toDto).toList()
        );
    }

    @PostMapping
    public ResponseEntity<GetUserDTO> save(@RequestBody @Validated(Post.class) PostUserDTO userDTO) {
        var saved = userService.save(userMapper.toEntity(userDTO));
        return ResponseEntity.created(URI.create("/users/%d".formatted(saved.getId())))
                .body(userMapper.toDto(saved));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<GetUserDTO> update(@PathVariable Long id, @RequestBody @Validated(Post.class) PostUserDTO userDTO) {
        if (!userService.existsById(id)) {
            throw new EntityNotFoundException();
        }
        var user = userMapper.toEntity(userDTO);
        user.setId(id);
        userService.save(user);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<GetUserDTO> patch(@PathVariable Long id, @RequestBody PostUserDTO userDTO) {
        return ResponseEntity.ok(userMapper.toDto(
                userService.save(
                        userMapper.toEntity(userDTO, userService.findById(id).orElseThrow(
                                () -> new EntityNotFoundException("User with id " + id + " not found")
                        ))
                )
        ));
    }

}
