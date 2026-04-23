package com.tpspringboot.apirestclientcommande.User.filter;

import com.tpspringboot.apirestclientcommande.User.configuration.JwtUtils;
import com.tpspringboot.apirestclientcommande.User.serviceCL.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtUtils jwtUtils;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        String method = request.getMethod();

        if ("OPTIONS".equalsIgnoreCase(method)) {
            return true;
        }

        // Ne pas filtrer les routes publiques d'auth
        return path.startsWith("/api/auth/");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String path = request.getServletPath();
        final String method = request.getMethod();
        final String jwt = extractToken(request, authHeader);

        log.debug("[JWT] {} {} | Authorization header present: {}", method, path, authHeader != null);

        if (jwt == null || jwt.isBlank()) {
            log.debug("[JWT] No token found in Authorization header or cookies -> anonymous request for {} {}", method, path);
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String username = jwtUtils.extractUsername(jwt);
            List<String> rolesFromToken = jwtUtils.extractRoles(jwt);

            log.debug("[JWT] username extracted: {}", username);
            log.debug("[JWT] roles from token: {}", rolesFromToken);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

                if (jwtUtils.validateToken(jwt, userDetails)) {

                    // Authorities depuis le token
                    List<SimpleGrantedAuthority> tokenAuthorities = rolesFromToken.stream()
                            .map(role -> role.startsWith("ROLE_")
                                    ? new SimpleGrantedAuthority(role)
                                    : new SimpleGrantedAuthority("ROLE_" + role))
                            .collect(Collectors.toList());

                    // Fallback: si token ne porte pas de rôles, utiliser ceux de UserDetails
                    Collection<? extends GrantedAuthority> finalAuthorities;
                    if (tokenAuthorities.isEmpty()) {
                        log.warn("[JWT] Token has no roles for user '{}', falling back to UserDetails authorities: {}",
                                username, userDetails.getAuthorities());
                        finalAuthorities = userDetails.getAuthorities();
                    } else {
                        finalAuthorities = tokenAuthorities;
                    }

                    log.debug("[JWT] Final authorities injected for '{}': {}", username, finalAuthorities);

                    UsernamePasswordAuthenticationToken authenticationToken =
                            UsernamePasswordAuthenticationToken.authenticated(userDetails, null, finalAuthorities);
                    authenticationToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    log.debug("[JWT] Authentication set in SecurityContext for user: {}", username);

                } else {
                    log.warn("[JWT] Token validation failed for user: {}", username);
                }
            }

        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            log.warn("[JWT] Token expired: {}", e.getMessage());
        } catch (io.jsonwebtoken.security.SignatureException e) {
            log.warn("[JWT] Invalid token signature: {}", e.getMessage());
        } catch (io.jsonwebtoken.MalformedJwtException e) {
            log.warn("[JWT] Malformed token: {}", e.getMessage());
        } catch (Exception e) {
            log.error("[JWT] Unexpected error processing token: {}", e.getMessage(), e);
        }

        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request, String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }

        Cookie[] cookies = request.getCookies();
        if (cookies == null || cookies.length == 0) {
            return null;
        }

        return Arrays.stream(cookies)
                .filter(cookie -> List.of("accessToken", "token", "jwt").contains(cookie.getName()))
                .map(Cookie::getValue)
                .filter(value -> value != null && !value.isBlank())
                .findFirst()
                .orElse(null);
    }
}