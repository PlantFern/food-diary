package com.github.plantfern.foodDiary.users.domain.security;

import com.github.plantfern.foodDiary.users.domain.entities.UserEntity;
import com.github.plantfern.foodDiary.users.domain.repositories.UserRepository;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.jspecify.annotations.NullMarked;


@Service
@NullMarked
public class SecurityUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    private SecurityUserDetailsService(UserRepository userRepository){
        this.userRepository = userRepository;
    }


    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity user = userRepository
                .findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UsernameNotFoundException(email));

        var authorities = user.getUserRoles()
                .stream()
                .map(
                        ur -> new SimpleGrantedAuthority("ROLE_" + ur.getRole().getName().name())
                )
                .toList();

        return User
                .withUsername(user.getEmail())
                .password(user.getHashPassword())
                .authorities(authorities)
                .disabled(!user.isEnabled())
                .build();
    }
}
