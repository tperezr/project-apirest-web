package com.alkemy.ong.security;
import java.io.IOException;
import java.util.Locale;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alkemy.ong.service.impl.UserServiceImpl;
import com.alkemy.ong.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@AllArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter{

    private static final String AUTHORIZATION = "Authorization";
    private static final String BEARER = "Bearer ";

    private final MessageSource messageSource;

    @Autowired
    private UserServiceImpl userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {


        try {

            final String autorizationHeader = request.getHeader(AUTHORIZATION);
            String username = null;
            String jwt = null;

            if (autorizationHeader != null && autorizationHeader.startsWith(BEARER)) {
                jwt = autorizationHeader.substring(7);
                username = jwtUtil.extractUsername(jwt);
            }


        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
            if (jwtUtil.validateToken(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }

        filterChain.doFilter(request, response);

        }catch (SignatureException ex){
            response.setContentType("application/json");
            response.getWriter().write(messageSource.getMessage("error.jwt.signatureException", new Object[] {"jwt"}, Locale.US));
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);//
            System.out.println("Invalid JWT Signature");

        }catch (MalformedJwtException ex){
            response.setContentType("application/json");
            response.getWriter().write(messageSource.getMessage("error.jwt.malformedJwtException", new Object[] {"jwt"}, Locale.US));
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);//
            System.out.println("Invalid JWT token");

        }catch (ExpiredJwtException ex){
            response.setContentType("application/json");
            response.getWriter().write(messageSource.getMessage("error.jwt.expiredJwtException", new Object[] {"jwt"}, Locale.US));
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);//
            System.out.println("Expired JWT token");

        }catch (UnsupportedJwtException ex){
            response.setContentType("application/json");
            response.getWriter().write(messageSource.getMessage("error.jwt.unsupportedJwtException", new Object[] {"jwt"}, Locale.US));
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);//
            System.out.println("Unsupported JWT exception");
        }catch (IllegalArgumentException ex){
            response.setContentType("application/json");
            response.getWriter().write(messageSource.getMessage("error.jwt.jwtClaimsStringIsEmpty", new Object[] {"jwt"}, Locale.US));
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);//
            System.out.println("Jwt claims string is empty");
        }
    }
}