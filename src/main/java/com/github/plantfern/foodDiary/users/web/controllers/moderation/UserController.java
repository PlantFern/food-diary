package com.github.plantfern.foodDiary.users.web.controllers.moderation;

import com.github.plantfern.foodDiary.users.api.UserDto;
import com.github.plantfern.foodDiary.users.domain.UserMapper;
import com.github.plantfern.foodDiary.users.domain.UserService;
import com.github.plantfern.foodDiary.users.web.requests.RoleRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;


@RestController
@RequestMapping("/api/moderation/users")
@PreAuthorize("hasAnyRole('MODERATOR', 'ADMINISTRATOR')")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @Autowired
    public UserController(UserService userService, UserMapper mapper) {
        this.userService = userService;
        this.userMapper = mapper;
    }


    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getById(@PathVariable Long userId){
        return ResponseEntity.ok(this.userService.findById(userId));
    }

    @GetMapping("/")
    public ResponseEntity<List<UserDto>> getAll() {
        return ResponseEntity.ok(
                userService.getAll()
                        .stream()
                        .map(userMapper::toDto).toList()
        );
    }

    @PreAuthorize("hasAnyRole('ADMINISTRATOR')")
    @PostMapping("/{targetUserId}/role")
    public ResponseEntity<Void> addRoleFor(@PathVariable Long targetUserId ,@ModelAttribute RoleRequest request) {
        userService.assignRoles(targetUserId, Set.of(request.roleName()));
        return ResponseEntity.noContent().build();
    }
}
