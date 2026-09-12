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
import com.github.plantfern.foodDiary.users.domain.security.UserPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@Transactional
public class UserService implements UserApi {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;

    private final SecurityCurrentUser currentUser;
    private final RoleAssignmentPolicy roleAssignmentPolicy;

    private final PasswordEncoder passwordEncoder;
    private final UserPolicy userPolicy;


    @Autowired
    public UserService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            UserMapper userMapper,

            SecurityCurrentUser currentUser,
            RoleAssignmentPolicy roleAssignmentPolicy,

            PasswordEncoder passwordEncoder,
            UserPolicy userPolicy){
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userMapper = userMapper;

        this.currentUser = currentUser;
        this.roleAssignmentPolicy = roleAssignmentPolicy;
        this.userPolicy = userPolicy;

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

    @Transactional
    public boolean hasRole(java.lang.Long userId, RoleName role) {
        return userRepository.findById(userId)
                .map(user -> user.getUserRoles().stream()
                        .anyMatch(ur -> ur.getRole().getName().equals(role)))
                .orElse(false);
    }

    @Transactional
    public UserDto findById(Long targetUserId) {
        var targetUser = this.findByIdInternal(targetUserId);

        userPolicy.ensureCanGet(currentUser, targetUser.id());

        return targetUser;
    }

    @Transactional
    public UserDto findByEmail(String email) {

        var targetUser = findByEmailInternal(email);

        userPolicy.ensureCanGet(currentUser, targetUser.id());

        return targetUser;
    }

    public void registerInternal(String email, String password) {
        if(userRepository.existsByEmail(email)){
            throw new IllegalArgumentException("Email already registered");
        }

        UserEntity user = new UserEntity(
                email,
                passwordEncoder.encode(password)
        );

        userRepository.save(user);
    }

    @Transactional
    public void assignRoles(Long targetUserId, Set<RoleName> roles){

        var actorUserId = currentUser.requireId();

        roleAssignmentPolicy.ensureCanAssign(currentUser, targetUserId, roles);

        assignRolesInternal(targetUserId, roles);
    }


    //region internal methods

    @Override
    @Transactional(readOnly = true)
    public boolean existsByIdInternal(java.lang.Long userId){
        return userRepository.existsById(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto findByIdInternal(Long targetUserId) {

        UserEntity targetUser = userRepository
                .findById(targetUserId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return userMapper.toDto(targetUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto findByEmailInternal(String email) {

        return userMapper.toDto(
                userRepository
                    .findByEmailIgnoreCase(email)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"))
        );
    }

    @Override
    public void assignRolesInternal(Long targetUserId, Set<RoleName> roles) {

        UserEntity targetUser = userRepository
                .findById(targetUserId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Set<RoleEntity> newRoles = roleRepository.findByNameIn(roles);

        targetUser.addRoles(newRoles);
        userRepository.save(targetUser);
    }
    //endregion
} // UserService
