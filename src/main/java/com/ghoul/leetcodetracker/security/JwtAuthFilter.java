package com.ghoul.leetcodetracker.security;

import com.ghoul.leetcodetracker.service.AuthService;
import com.ghoul.leetcodetracker.exception.ApiErrorWriter;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

    private final AuthService authService;
    private final ApiErrorWriter apiErrorWriter;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain
    ) throws ServletException, IOException {
        String token = getToken(request);
        if (token != null) {
            try {
                UserDetails userDetails = authService.validateToken(token);
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                SecurityContextHolder.getContext().setAuthentication(auth);

                if (userDetails instanceof TrackerUserDetails) {
                    request.setAttribute("userId", ((TrackerUserDetails) userDetails).getId());
                }
            } catch (JwtException | IllegalArgumentException | UsernameNotFoundException exception) {
                SecurityContextHolder.clearContext();
                log.debug("Rejected bearer token: {}", exception.getClass().getSimpleName());
                apiErrorWriter.write(response, HttpServletResponse.SC_UNAUTHORIZED,
                        "Unauthorized", "Bearer token is invalid or expired", request.getRequestURI());
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    private String getToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if(bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }else {
            return null;
        }
    }
}
