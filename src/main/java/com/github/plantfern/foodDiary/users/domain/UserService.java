package com.github.plantfern.foodDiary.users.domain;

import com.github.plantfern.foodDiary.users.api.RoleName;
import com.github.plantfern.foodDiary.users.api.UserApi;
import com.github.plantfern.foodDiary.users.api.UserDto;
import com.github.plantfern.foodDiary.users.domain.entities.RoleEntity;
import com.github.plantfern.foodDiary.users.domain.entities.UserEntity;
import com.github.plantfern.foodDiary.users.domain.repositories.RoleRepository;
import com.github.plantfern.foodDiary.users.domain.repositories.UserRepository;
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
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Autowired
    public UserService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            UserMapper userMapper){
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
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
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserDto> findByEmail(String email) {
        return userRepository
                .findByEmailIgnoreCase(email)
                .map(userMapper::toDto);
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
    public void assignRoles(Long userId, Set<RoleName> roles) {
        UserEntity user = userRepository
                .findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Set<RoleEntity> newRoles = roleRepository.findByNameIn(roles);
        user.replaceRoles(newRoles);
    }

    @Override
    public boolean hasRole(Long userId, RoleName role) {
        return userRepository.findById(userId)
                .map(user -> user.getUserRoles().stream()
                        .anyMatch(ur -> ur.getRole().getName().equals(role)))
                .orElse(false);
    }
} // UserService
