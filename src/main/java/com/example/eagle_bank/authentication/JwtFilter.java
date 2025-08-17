package com.example.eagle_bank.authentication;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtFilter extends HttpFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        String authHeader = request.getHeader("Authorization");

        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);

                if (!jwtUtil.isTokenExpired(token)) {
                    Long userId = jwtUtil.extractUserId(token);
                    String email = jwtUtil.extractEmail(token);
                    UserPrincipal principal = new UserPrincipal(userId, email);

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(principal, null, Collections.emptyList());

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    throw new ServletException("Invalid JWT token");
                }
            }

            chain.doFilter(request, response);
        } catch (ExpiredJwtException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\":\"JWT token has expired\"}");
        } catch (JwtException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\":\"Invalid JWT token\"}");
        }
    }
}
