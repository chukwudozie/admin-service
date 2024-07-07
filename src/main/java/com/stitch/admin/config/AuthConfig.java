package com.stitch.admin.config;

import com.auth0.jwt.algorithms.Algorithm;
import com.stitch.admin.model.entity.AdminUser;
import com.stitch.admin.repository.AdminUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.stream.Collectors;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class AuthConfig {

    private final AdminUserRepository userRepository;



    @Bean
    public UserDetailsService userDetailsService(){
        return email -> {
            AdminUser users = userRepository.findAdminUserByEmailAddress(email).orElseThrow(
                    () -> new UsernameNotFoundException("Username not found"));
            log.info("username from user details ==> {}",email);
            List<GrantedAuthority> authorities = users.getRoles().stream()
                    .flatMap(role -> role.getPermissions().stream())
                            .map(permission -> new SimpleGrantedAuthority(permission.getName()))
                            .collect(Collectors.toList());
//                    .map(role ->(GrantedAuthority) new SimpleGrantedAuthority(role.getName())).toList();
            log.info("Authorities ==> {}",authorities);
            var securedUser = new User(users.getEmailAddress(),users.getPassword(),authorities);
            log.info("loaded User {}",securedUser);
            return securedUser;
        };
    }

    @Bean
    public Algorithm algorithm(){
        return Algorithm.HMAC256("12345567890");
    }


    @Bean
    public BCryptPasswordEncoder encoder(){
        return new BCryptPasswordEncoder();
    }

}