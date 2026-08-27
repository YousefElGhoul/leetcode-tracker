package com.ghoul.leetcodetracker.service;

import com.ghoul.leetcodetracker.exception.DuplicateUsernameException;
import com.ghoul.leetcodetracker.model.entities.User;
import com.ghoul.leetcodetracker.repositories.UserRepo;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import io.jsonwebtoken.ExpiredJwtException;
import java.time.Duration;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuthServiceTest {
    private final UserRepo repo = mock(UserRepo.class);
    private final AuthService service = new AuthService(
            mock(AuthenticationManager.class), mock(UserDetailsService.class), repo,
            PasswordEncoderFactories.createDelegatingPasswordEncoder());

    @Test
    void registrationHashesPassword() {
        when(repo.findByUsername("alice")).thenReturn(Optional.empty());

        service.register("alice", "password123");

        verify(repo).save(argThat((User user) ->
                user.getPassword().startsWith("{bcrypt}") && !user.getPassword().contains("password123")));
    }

    @Test
    void duplicateRegistrationIsConflict() {
        when(repo.findByUsername("alice")).thenReturn(Optional.of(new User()));

        assertThatThrownBy(() -> service.register("alice", "password123"))
                .isInstanceOf(DuplicateUsernameException.class);
    }

    @Test
    void expiredTokenIsRejected() {
        ReflectionTestUtils.setField(service, "secretKey",
                "dGVzdC1vbmx5LXNlY3JldC10aGF0LWlzLWF0LWxlYXN0LTMyLWJ5dGVzLWxvbmc=");
        ReflectionTestUtils.setField(service, "jwtExpiry", Duration.ofSeconds(-1));
        UserDetails user = org.springframework.security.core.userdetails.User
                .withUsername("alice").password("unused").authorities("USER").build();
        String token = service.generateToken(user);

        assertThatThrownBy(() -> service.validateToken(token))
                .isInstanceOf(ExpiredJwtException.class);
    }
}
