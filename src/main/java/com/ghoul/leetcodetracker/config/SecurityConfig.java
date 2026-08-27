package com.ghoul.leetcodetracker.config;

import com.ghoul.leetcodetracker.repositories.UserRepo;
import com.ghoul.leetcodetracker.security.JwtAuthFilter;
import com.ghoul.leetcodetracker.security.TrackerUserDetailsService;
import com.ghoul.leetcodetracker.service.AuthService;
import com.ghoul.leetcodetracker.exception.ApiErrorWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Bean
    public JwtAuthFilter jwtAuthFilter(AuthService authService, ApiErrorWriter apiErrorWriter){
        return new JwtAuthFilter(authService, apiErrorWriter);
    }

    @Bean
    public UserDetailsService userDetailsService(UserRepo userRepo){
        return new TrackerUserDetailsService(userRepo);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthFilter jwtAuthFilter,
            ApiErrorWriter apiErrorWriter
    ) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                     .requestMatchers(HttpMethod.DELETE, "/api/v1/clear").authenticated()
                     .anyRequest().permitAll()
            )
            .csrf(csrf ->
                    csrf.disable()
            )
            .cors(cors -> {})
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .exceptionHandling(exceptions -> exceptions
                    .authenticationEntryPoint((request, response, exception) -> apiErrorWriter.write(
                            response, 401, "Unauthorized", "Authentication is required", request.getRequestURI()))
                    .accessDeniedHandler((request, response, exception) -> apiErrorWriter.write(
                            response, 403, "Forbidden", "Access is denied", request.getRequestURI())))
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
