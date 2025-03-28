package com.pks.spring_sms_backend.config;



import com.pks.spring_sms_backend.service.JWTService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;




@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JWTService jwtService;

    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JWTService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;

        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");

        if(authHeader== null || !authHeader.startsWith("Bearer ")){
            filterChain.doFilter(request,response);
            return;
        }

        final String jwtToken = authHeader.substring(7);
        final String username =jwtService.extractUserName(jwtToken);

        Authentication authentication = SecurityContextHolder
                .getContext().getAuthentication();
        if(username != null && authentication == null){
            UserDetails userDetails
                    = userDetailsService.loadUserByUsername(username);
            System.out.println("User Name" + userDetails.getUsername());
            if(jwtService.isTokenValid(jwtToken,userDetails)){

                UsernamePasswordAuthenticationToken  authenticationToken=
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );
                authenticationToken.setDetails(
                        new WebAuthenticationDetailsSource()
                                .buildDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(
                        authenticationToken
                );
            }
        }
        filterChain.doFilter(request,response);
    }
}
