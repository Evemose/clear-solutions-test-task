package org.users.core;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.users.core.model.UserMapper;
import org.users.core.model.dto.GetUserDTO;
import org.users.core.model.dto.PostUserDTO;
import org.users.core.model.entities.User;

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
                userService.findById(id).map(userMapper::toDto).orElseThrow(EntityNotFoundException::new)
        );
    }

    @GetMapping
    public ResponseEntity<List<GetUserDTO>> getAll(@RequestParam(defaultValue = "1900-01-01") LocalDate minBirthdate,
                                             @RequestParam(defaultValue = "9999-12-31") LocalDate maxBirthdate) {
        return ResponseEntity.ok(
                userService.findByBirthdateBetween(minBirthdate, maxBirthdate).stream().map(userMapper::toDto).toList()
        );
    }

    @PostMapping
    public ResponseEntity<GetUserDTO> save(PostUserDTO userDTO) {
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
    public ResponseEntity<GetUserDTO> update(@PathVariable Long id, PostUserDTO userDTO) {
        if (!userService.existsById(id)) {
            throw new EntityNotFoundException();
        }
        var user = userMapper.toEntity(userDTO);
        user.setId(id);
        userService.save(user);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<GetUserDTO> patch(@PathVariable Long id, PostUserDTO userDTO) {
        return ResponseEntity.ok(userMapper.toDto(
                userService.save(
                        userMapper.toEntity(userDTO, userService.findById(id).orElseThrow(EntityNotFoundException::new))
                )
        ));
    }

}
