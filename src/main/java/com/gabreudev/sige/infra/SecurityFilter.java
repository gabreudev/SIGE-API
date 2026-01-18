package com.gabreudev.sige.infra;


import com.gabreudev.sige.entities.user.User;
import com.gabreudev.sige.repositories.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@RequiredArgsConstructor
@Component
public class SecurityFilter extends OncePerRequestFilter {

    private final TokenService tokenService;

    private final UserRepository userRepository;

    private String recoverToken(HttpServletRequest request){
        var authHeader = request.getHeader("Authorization");
        if(authHeader == null) return null;
        return authHeader.replace("Bearer ", "");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var token = this.recoverToken(request);
        if(token != null){
            var userId = tokenService.validateToken(token);
            if(userId != null && !userId.isEmpty()){
                try {
                    UUID id = UUID.fromString(userId);
                    User user = userRepository.findById(id).orElse(null);

                    if(user != null){
                        var authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                } catch (IllegalArgumentException e) {
                    // Invalid UUID format, skip authentication
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}