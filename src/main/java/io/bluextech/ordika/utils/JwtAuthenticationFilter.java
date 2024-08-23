package io.bluextech.ordika.utils;
/* Created by limxuanhui on 20/8/24 */

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // Bearer <token>
        final String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            username = jwtUtil.extractUsername(token);
        }

//        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//            UserDetails userDetails = User.withUsername("Joseph").password("password123").build();
//            if (jwtUtil.validateToken(token)) {
//                // If token is valid => token not expired, username in db
//
//                JwtAuthenticationToken jwtAuthToken = new JwtAuthenticationToken(
//                        Jwt.withTokenValue(jwtUtil.generateJwt(userDetails)).build());
//                SecurityContextHolder.getContext().setAuthentication(jwtAuthToken);
//            }
//        }
        System.out.println("\n");
        System.out.println("Filtering Jwt: " + token + username);
        System.out.println("\n");
        filterChain.doFilter(request, response);
    }

}
