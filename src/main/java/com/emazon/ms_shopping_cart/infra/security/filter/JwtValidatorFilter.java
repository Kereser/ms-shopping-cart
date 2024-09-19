package com.emazon.ms_shopping_cart.infra.security.filter;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.emazon.ms_shopping_cart.infra.security.entrypoint.CustomJWTEntryPoint;
import com.emazon.ms_shopping_cart.infra.security.utils.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
public class JwtValidatorFilter extends OncePerRequestFilter {

    private final CustomJWTEntryPoint customJWTEntryPoint;

    private static final String UNEXPECTED_EXCEPTION = "Unexpected exception";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String jwtToken = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (jwtToken == null || jwtToken.substring(0,5).equalsIgnoreCase("basic")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            jwtToken = jwtToken.substring(7);

            DecodedJWT decodedJWT = JwtUtils.validateToken(jwtToken);

            String username = JwtUtils.getUsername(decodedJWT);
            String authorityList = JwtUtils.getAuthorities(decodedJWT);

            List<GrantedAuthority> authorities = AuthorityUtils.commaSeparatedStringToAuthorityList(authorityList);

            SecurityContext context = SecurityContextHolder.createEmptyContext();
            Authentication auth = new UsernamePasswordAuthenticationToken(username, null, authorities);
            context.setAuthentication(auth);
            SecurityContextHolder.setContext(context);
        } catch (AuthenticationException ex) {
            customJWTEntryPoint.commence(request, response, ex);
            return;
        } catch (Exception ex) {
            customJWTEntryPoint.commence(request, response, new InternalAuthenticationServiceException(UNEXPECTED_EXCEPTION, ex));
            return;
        }

        filterChain.doFilter(request, response);
    }
}
