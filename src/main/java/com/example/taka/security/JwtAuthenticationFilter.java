package com.example.taka.security;

import com.example.taka.services.CustomUserDetailsService;
import io.jsonwebtoken.Claims;


import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;

import java.io.IOException;
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain

    ) throws ServletException, IOException{

        //get auth header & check if it starts with "Bearer"
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        String email = null;
        String jwtToken = null;

        if(authHeader != null && authHeader.startsWith("Bearer")){
            jwtToken = authHeader.substring(7);

            //validate token & extract email
//            if(jwtUtil.validateToken(jwtToken)){
//                email = jwtUtil.ExtractEmail(jwtToken);
//            }
            try{
                email = jwtUtil.ExtractEmail(jwtToken);
            }catch(JwtException | IllegalArgumentException ex){
                logger.warn("invalid JWT: ");
            }
        }

        //if it has email but no exisiting authentication, load user details
        if(email != null && SecurityContextHolder.getContext().getAuthentication() ==null){
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            if(jwtUtil.validateToken(jwtToken, userDetails.getUsername())){

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authToken);
            }


        }
        //continue thr filter chain
        filterChain.doFilter(request, response);
    }

}
