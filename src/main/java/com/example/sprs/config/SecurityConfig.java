package com.example.sprs.config;

import com.example.sprs.config.JwtFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);

    @Autowired
    private JwtFilter jwtFilter;

    /** 🔐 BCrypt password encoder bean (recommended for real apps) */
    @Bean
    public PasswordEncoder passwordEncoder() {
        log.debug("🔧 Initializing BCrypt password encoder");
        return new BCryptPasswordEncoder();
    }

    /** 🔐 Expose AuthenticationManager bean */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        log.debug("🔧 Retrieving AuthenticationManager from configuration");
        return config.getAuthenticationManager();
    }

    /** 🔐 Define security rules and register JWT filter */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        log.debug("🔒 Configuring HTTP security");

        http
                .csrf(csrf -> {
                    log.debug("❌ CSRF disabled (stateless API)");
                    csrf.disable();
                })
                .authorizeHttpRequests(auth -> {
                    log.debug("✅ Public paths: /api/users/register, /api/auth/**, /api/public/**");
                    auth
                            .requestMatchers(
                                    "/api/users/register",
                                    "/api/auth/**",
                                    "/api/public/**"
                            ).permitAll()
                            .anyRequest().authenticated();
                    log.debug("🔐 All other endpoints require authentication");
                })
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        log.debug("✅ JWT filter added to the filter chain");

        return http.build();
    }
}
