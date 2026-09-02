package com.github.plantfern.foodDiary.users.domain;

import com.github.plantfern.foodDiary.users.api.RoleName;
import com.github.plantfern.foodDiary.users.api.UserApi;
import com.github.plantfern.foodDiary.users.api.UserDto;
import com.github.plantfern.foodDiary.users.domain.entities.RoleEntity;
import com.github.plantfern.foodDiary.users.domain.entities.UserEntity;
import com.github.plantfern.foodDiary.users.domain.repositories.RoleRepository;
import com.github.plantfern.foodDiary.users.domain.repositories.UserRepository;
import com.github.plantfern.foodDiary.users.domain.security.RoleAssignmentPolicy;
import com.github.plantfern.foodDiary.users.domain.security.SecurityCurrentUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class UserService implements UserApi {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;

    private final SecurityCurrentUser securityCurrentUser;
    private final RoleAssignmentPolicy roleAssignmentPolicy;

    private final PasswordEncoder passwordEncoder;
    private final UserGettingPolicy userGettingPolicy;


    @Autowired
    public UserService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            UserMapper userMapper,

            SecurityCurrentUser securityCurrentUser,
            RoleAssignmentPolicy roleAssignmentPolicy,

            PasswordEncoder passwordEncoder,
            UserGettingPolicy userGettingPolicy){
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userMapper = userMapper;

        this.securityCurrentUser = securityCurrentUser;
        this.roleAssignmentPolicy = roleAssignmentPolicy;
        this.userGettingPolicy = userGettingPolicy;

        this.passwordEncoder = passwordEncoder;
    } // UserService

    public void save(UserEntity user){
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public List<UserEntity> getAll() {
        return userRepository
                .findAllByDeletedAtIsNull();
    }


    // реализация интерфейса
    @Override
    @Transactional(readOnly = true)
    public Optional<UserDto> findById(Long id) {
        return userRepository
                .findById(id)
                .map(userMapper::toDto);
    public UserDto findById(Long targetUserId) {
        Long actorUserId = securityCurrentUser.requireId();

        UserEntity actorUser = userRepository
                .findById(actorUserId)
                .orElseThrow(() -> new IllegalArgumentException("Actor not found"));
        UserEntity targetUser = userRepository
                .findById(targetUserId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        userGettingPolicy.ensureCanGet(actorUser, targetUser);

        return userMapper.toDto(targetUser);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserDto> findByEmail(String email) {
        return userRepository
    public UserDto findByEmail(String email) {
        Long actorUserId = securityCurrentUser.requireId();

        UserEntity actorUser = userRepository
                .findById(actorUserId)
                .orElseThrow(() -> new IllegalArgumentException("Actor not found"));
        UserEntity targetUser = userRepository
                .findByEmailIgnoreCase(email)
                .map(userMapper::toDto);
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        userGettingPolicy.ensureCanGet(actorUser, targetUser);

        return userMapper.toDto(targetUser);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById (Long userId){
        return userRepository.existsById(userId);
    }

    @Override
    public UserDto register(String email, String password) {
        if(userRepository.existsByEmail(email)){
            throw new IllegalArgumentException("Email alreade registered");
        }

        UserEntity user = new UserEntity(
                email,
                passwordEncoder.encode(password)
        );

        RoleEntity defaultRole = roleRepository
                .findByName(RoleName.USER)
                .orElseThrow(() -> new IllegalStateException("Role user is missing"));
        user.addRole(defaultRole);

        UserEntity saved = userRepository.save(user);
        return userMapper.toDto(saved);
    }

    @Override
    public void assignRoles(Long targetUserId, Set<RoleName> roles) {
        Long actorUserId = securityCurrentUser.requireId();

        UserEntity actorUser = userRepository
                .findById(actorUserId)
                .orElseThrow(() -> new IllegalArgumentException("Actor not found"));
        UserEntity targetUser = userRepository
                .findById(targetUserId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        roleAssignmentPolicy.ensureCanAssign(actorUser, targetUser, roles);

        Set<RoleEntity> newRoles = roleRepository.findByNameIn(roles);

        targetUser.addRoles(newRoles);
        userRepository.save(targetUser);
    }

    @Override
    public boolean hasRole(Long userId, RoleName role) {
        return userRepository.findById(userId)
                .map(user -> user.getUserRoles().stream()
                        .anyMatch(ur -> ur.getRole().getName().equals(role)))
                .orElse(false);
    }
} // UserService
