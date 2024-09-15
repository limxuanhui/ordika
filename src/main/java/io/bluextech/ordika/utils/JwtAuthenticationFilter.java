package io.bluextech.ordika.utils;
/* Created by limxuanhui on 20/8/24 */

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private JwtDecoder jwtDecoder;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");

        String token = null;
        String subject = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            subject = jwtUtil.extractSubject(token);
        }

        if (subject != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // This validation has a redundant check of subject
            // TODO: consider adding userId (to be used as subject here) in header on frontend
            // If token is valid => token not expired
            if (jwtUtil.isTokenValid(token, subject)) {
                Jwt jwt = jwtDecoder.decode(token);
                JwtAuthenticationToken jwtAuthToken = new JwtAuthenticationToken(jwt);
                SecurityContextHolder.getContext().setAuthentication(jwtAuthToken);
            }
        }

        filterChain.doFilter(request, response);
    }

}
