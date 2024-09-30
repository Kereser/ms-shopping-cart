package com.emazon.ms_shopping_cart.infra.security.filter;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.emazon.ms_shopping_cart.ConsUtils;
import com.emazon.ms_shopping_cart.infra.security.entrypoint.CustomJWTEntryPoint;
import com.emazon.ms_shopping_cart.infra.security.model.CustomUserDetails;
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
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
public class JwtValidatorFilter extends OncePerRequestFilter {

    private final CustomJWTEntryPoint customJWTEntryPoint;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String jwtToken = request.getHeader(HttpHeaders.AUTHORIZATION);

        try {
            if (jwtToken != null && !StringUtils.startsWithIgnoreCase(jwtToken, ConsUtils.BASIC)) {
                jwtToken = jwtToken.substring(ConsUtils.SUBSTRING_INDEX);

                DecodedJWT decodedJWT = JwtUtils.validateToken(jwtToken);

                String username = JwtUtils.getUsername(decodedJWT);
                String authorityList = JwtUtils.getAuthorities(decodedJWT);
                Long userId = JwtUtils.getClaimAs(ConsUtils.USER_ID, decodedJWT, Long.class);
                List<GrantedAuthority> authorities = AuthorityUtils.commaSeparatedStringToAuthorityList(authorityList);

                CustomUserDetails userDetails = new CustomUserDetails(username, ConsUtils.PASSWORD, authorities, userId);

                SecurityContext context = SecurityContextHolder.createEmptyContext();
                Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, jwtToken, authorities);
                context.setAuthentication(auth);
                SecurityContextHolder.setContext(context);
            }
        } catch (AuthenticationException ex) {
            customJWTEntryPoint.commence(request, response, ex);
            return;
        } catch (Exception ex) {
            customJWTEntryPoint.commence(request, response, new InternalAuthenticationServiceException(ConsUtils.UNEXPECTED_EXCEPTION, ex));
            return;
        }

        filterChain.doFilter(request, response);
    }
}
