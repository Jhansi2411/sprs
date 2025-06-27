package com.example.sprs.config;

import com.example.sprs.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtFilter.class);
    private final JwtUtil jwtUtil;

    private static final List<String> PUBLIC_ENDPOINTS = List.of(
            "/api/users/register", "/api/auth/login"
    );

    public JwtFilter(@NonNull JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain chain) throws ServletException, IOException {

        String path = request.getRequestURI();
        log.debug("🔍 Incoming request path: {}", path);

        // 🔓 Allow public endpoints without JWT
        if (isPublicEndpoint(path)) {
            log.debug("✅ Public endpoint detected: {}", path);
            chain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        log.debug("🪪 Authorization Header: {}", authHeader);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            log.debug("🔐 Bearer token extracted: {}", token);

            try {
                Claims claims = jwtUtil.validateToken(token);
                String username = claims.getSubject();
                String role = claims.get("role", String.class);

                log.debug("✅ Token validated. Username: {}, Role: {}", username, role);

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        username,
                        null,
                        Collections.singleton(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                );

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.debug("🔓 Security context updated with authenticated user: {}", username);

            } catch (Exception ex) {
                log.error("❌ JWT validation failed: {}", ex.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid or expired token");
                return;
            }

        } else {
            log.warn("🚫 Missing or invalid Authorization header");
        }

        chain.doFilter(request, response);
    }


    private boolean isPublicEndpoint(String path) {
        boolean isPublic = PUBLIC_ENDPOINTS.stream().anyMatch(p ->
                path.equals(p) || path.startsWith(p + "/") || path.startsWith(p + "?")
        );
        log.debug("🛡️ Checking if path '{}' is public: {}", path, isPublic);
        return isPublic;
    }

}