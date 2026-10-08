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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
    }


    @Transactional
    public void save(UserEntity user){
        userRepository.save(user);
    }


    @Transactional(readOnly = true)
    public List<UserEntity> getAll() {

        return userRepository
                .findAllByDeletedAtIsNull();
    }

    @Transactional(readOnly = true)
    public boolean hasRole(java.lang.Long userId, RoleName role) {

        return userRepository.findById(userId)
                .map(user -> user.getUserRoles().stream()
                        .anyMatch(ur -> ur.getRole().getName().equals(role)))
                .orElse(false);
    }

    @Transactional
    public UserDto getById(Long targetUserId) {

        var targetUser = this.getByIdInternal(targetUserId);

        userPolicy.ensureCanGet(currentUser, targetUser.id());

        return targetUser;
    }

    @Transactional(readOnly = true)
    public UserDto getMyProfile() {

        return this.getByIdInternal(currentUser.requireId());
    }

    @Transactional(readOnly = true)
    public UserDto getByEmail(String email) {

        var targetUser = getByEmailInternal(email);

        userPolicy.ensureCanGet(currentUser, targetUser.id());

        return targetUser;
    }

    @Transactional
    public UserDto register(
            String email,
            String password,
            String login
    ) {

        var emailNormalized = email.trim().toLowerCase();
        var loginNormalized =
                login != null && !login.isEmpty()
                        ? login.trim().toLowerCase()
                        : login;

        if (userRepository.existsByEmailIgnoreCase(emailNormalized)) {
            throw new IllegalArgumentException("Email already registered");
        }

        if (userRepository.existsByLoginIgnoreCase(loginNormalized)) {
            throw new IllegalArgumentException("Login already used");
        }

        UserEntity user;

        if (loginNormalized != null && !loginNormalized.isEmpty()) {
            user = new UserEntity(
                    emailNormalized,
                    loginNormalized,
                    passwordEncoder.encode(password)
            );
        } else {
            user = new UserEntity(
                    emailNormalized,
                    passwordEncoder.encode(password)
            );
        }

        try {
            return userMapper.toDto(userRepository.save(user));
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException("Email or login already registered", ex);
        }
    }

    @Transactional
    public void assignRoles(Long targetUserId, Set<RoleName> roles){

        roleAssignmentPolicy.ensureCanAssign(currentUser, targetUserId, roles);

        assignRolesInternal(targetUserId, roles);
    }

    @Transactional
    public void softDelete(UserEntity user){

        var email = user.getEmail();
        var login = user.getLogin();

        user.softDelete();

        var prefix = "deleted_" + user.getId() + "_";

        user.setEmail(prefix + email);
        if(login != null && !login.isBlank())
            user.setLogin(prefix + login);

        userRepository.save(user);
    }

    @Transactional
    public void restore(UserEntity user){

        var email = user.getEmail();
        var login = user.getLogin();

        var prefix = "deleted_" + user.getId() + "_";
        var prefixLength = prefix.length();

        if(email.startsWith(prefix)){
            email = email.substring(prefixLength);
        }
        if(userRepository.existsByEmailIgnoreCase(email))
            throw new IllegalStateException("Email already in use");
        if(login != null && !login.isBlank()) {
            if (login.startsWith(prefix)) {
                login = login.substring(prefixLength);
            }
            if (userRepository.existsByLoginIgnoreCase(login))
                login = null;
        }
        user.setEmail(email);
        user.restore();
        userRepository.save(user);
    }


    //region internal methods

    @Override
    @Transactional(readOnly = true)
    public boolean existsByIdInternal(Long userId){

        return userRepository.existsById(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getByIdInternal(Long targetUserId) {

        UserEntity targetUser = userRepository
                .findById(targetUserId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return userMapper.toDto(targetUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getByEmailInternal(String email) {

        return userMapper.toDto(
                userRepository
                    .findByEmailIgnoreCase(email)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"))
        );
    }

    @Override
    @Transactional
    public void assignRolesInternal(Long targetUserId, Set<RoleName> roles) {

        UserEntity targetUser = userRepository
                .findById(targetUserId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Set<RoleEntity> newRoles = roleRepository.findByNameIn(roles);

        targetUser.addRoles(newRoles);
        userRepository.save(targetUser);
    }
    //endregion
}
