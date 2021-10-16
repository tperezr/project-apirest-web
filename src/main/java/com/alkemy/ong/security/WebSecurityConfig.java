package com.alkemy.ong.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.alkemy.ong.util.ERole;

import lombok.AllArgsConstructor;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@AllArgsConstructor
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserDetailsService userDetailsService;

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Override
    protected void configure(HttpSecurity http) throws Exception {

		http.csrf().disable();
    	http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.authorizeRequests()
    		.antMatchers("/auth/register/**", "/auth/login/**").permitAll()
    		.antMatchers("/api/docs/**", "/api/swagger-ui/**", "/v3/api-docs/**").permitAll()
            .antMatchers(HttpMethod.GET, "/organization/public").hasAnyAuthority(ERole.ROLE_ADMIN.name(), ERole.ROLE_USER.name())
            .antMatchers(HttpMethod.POST, "/organization/public").hasAnyAuthority(ERole.ROLE_ADMIN.name())
            .antMatchers(HttpMethod.POST, "/activities").hasAnyAuthority(ERole.ROLE_ADMIN.name())
            .antMatchers(HttpMethod.PUT, "/activities/{id}").hasAnyAuthority(ERole.ROLE_ADMIN.name())
            .antMatchers(HttpMethod.GET, "/activities").hasAnyAuthority(ERole.ROLE_USER.name())
            .antMatchers(HttpMethod.POST, "/contacts").hasAnyAuthority(ERole.ROLE_USER.name())
            .antMatchers(HttpMethod.GET, "/members").hasAnyAuthority(ERole.ROLE_ADMIN.name())
            .antMatchers(HttpMethod.GET, "/testimonials").hasAnyAuthority(ERole.ROLE_USER.name(), ERole.ROLE_ADMIN.name())
            .antMatchers(HttpMethod.DELETE, "/members/{id}").hasAnyAuthority(ERole.ROLE_ADMIN.name())
            .antMatchers(HttpMethod.POST, "/members").hasAnyAuthority(ERole.ROLE_USER.name())
            .antMatchers(HttpMethod.POST, "/slides").hasAnyAuthority(ERole.ROLE_ADMIN.name())
            .antMatchers(HttpMethod.PUT, "members/{id}").hasAnyAuthority(ERole.ROLE_USER.name())
            .antMatchers(HttpMethod.GET, "/comments").hasAnyAuthority(ERole.ROLE_ADMIN.name())
            .antMatchers(HttpMethod.POST, "/comments").hasAnyAuthority(ERole.ROLE_ADMIN.name(), ERole.ROLE_USER.name())
            .antMatchers(HttpMethod.PUT, "/comments/{id}").hasAnyAuthority(ERole.ROLE_USER.name(),ERole.ROLE_ADMIN.name())
            .antMatchers(HttpMethod.DELETE, "/comments/{id}").hasAnyAuthority(ERole.ROLE_ADMIN.name(), ERole.ROLE_USER.name())
            .antMatchers(HttpMethod.GET, "posts/{id}/comments").hasAnyAuthority(ERole.ROLE_USER.name())
            .antMatchers("/slides").hasAnyAuthority(ERole.ROLE_USER.name())
            .antMatchers("/testimonials").hasAnyAuthority(ERole.ROLE_ADMIN.name())
            .antMatchers("/testimonials/{id}").hasAnyAuthority(ERole.ROLE_ADMIN.name())
            .antMatchers("/contacts").hasAnyAuthority(ERole.ROLE_ADMIN.name())
            .antMatchers("/slides/{id}").hasAnyAuthority(ERole.ROLE_ADMIN.name())
            .antMatchers("/users","/users/**").hasAnyAuthority(ERole.ROLE_ADMIN.name())
            .antMatchers("/auth/me").hasAnyAuthority(ERole.ROLE_ADMIN.name(),ERole.ROLE_USER.name())
            .antMatchers("/categories/{id}").hasAnyAuthority(ERole.ROLE_ADMIN.name())
            .antMatchers("/categories").hasAnyAuthority(ERole.ROLE_ADMIN.name())
            .antMatchers("/news/{id}").hasAnyAuthority(ERole.ROLE_ADMIN.name())
            .antMatchers(HttpMethod.GET, "/news").hasAnyAuthority(ERole.ROLE_ADMIN.name(), ERole.ROLE_USER.name())
            .antMatchers(HttpMethod.POST, "/news").hasAnyAuthority(ERole.ROLE_ADMIN.name())
            .anyRequest().authenticated();
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class); //Add filters for JWT
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(daoAuthenticationProvider());
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(bCryptPasswordEncoder);
        provider.setUserDetailsService(userDetailsService);
        return provider;
    }
}
