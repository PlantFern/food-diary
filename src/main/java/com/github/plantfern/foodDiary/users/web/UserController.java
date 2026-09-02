package com.github.plantfern.foodDiary.users.web;

import com.github.plantfern.foodDiary.users.api.UserDto;
import com.github.plantfern.foodDiary.users.domain.UserMapper;
import com.github.plantfern.foodDiary.users.domain.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;


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


    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getById(@PathVariable Long userId){
        return ResponseEntity.ok(this.userService.findById(userId));
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

    @PostMapping("/{targetUserId}/role")
    public ResponseEntity<Void> addRoleFor(@PathVariable Long targetUserId ,@RequestBody RoleRequest request) {
        userService.assignRoles(targetUserId, Set.of(request.roleName()));
        return ResponseEntity.noContent().build();
    }
} // UserController
