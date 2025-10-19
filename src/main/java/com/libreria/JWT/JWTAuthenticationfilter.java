package com.libreria.JWT;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JWTAuthenticationfilter extends OncePerRequestFilter {

    private final  JwtService jwt_service;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterchain)
        // Lógica para autenticar el token JWT en la solicitud
        throws ServletException, IOException {

        final String token = getTokenFromRequest(request);
        final String userEmail;
        if (token == null)
        {
            filterchain.doFilter(request, response);
            return;
        }
            userEmail=jwt_service.getUsernameFromToken(token);

            if (userEmail!=null && SecurityContextHolder.getContext().getAuthentication()==null)
            {
                UserDetails userDetails=userDetailsService.loadUserByUsername(userEmail);

                if (jwt_service.isTokenValid(token, userDetails))
                {
                    UsernamePasswordAuthenticationToken authToken= new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities());

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }

            }

            filterchain.doFilter(request, response);
        }
    private String getTokenFromRequest(HttpServletRequest request) {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if(StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer "))
        {
            return authHeader.substring(7);
        }
        return null;
    }
}

