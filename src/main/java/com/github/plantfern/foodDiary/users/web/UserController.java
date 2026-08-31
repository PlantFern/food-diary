package com.github.plantfern.foodDiary.users.web;

import com.github.plantfern.foodDiary.users.api.UserDto;
import com.github.plantfern.foodDiary.users.domain.UserMapper;
import com.github.plantfern.foodDiary.users.domain.UserService;
import com.github.plantfern.foodDiary.users.domain.repositories.UserRepository;
import jakarta.persistence.PrePersist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @Autowired
    public UserController(UserService userService, UserMapper mapper) {
        this.userService = userService;
        this.userMapper = mapper;
    } // UserController


    @PreAuthorize("hasAnyRole('MODERATOR', 'ADMINISTRATOR')")
    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getById(@PathVariable Long userId){
        return this.userService.findById(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAnyRole('MODERATOR', 'ADMINISTRATOR')")
    @GetMapping("/")
    public ResponseEntity<List<UserDto>> getAll() {
        return ResponseEntity.ok(
                userService.getAll()
                        .stream()
                        .map(userMapper::toDto).toList()
        );
    }
} // UserController
