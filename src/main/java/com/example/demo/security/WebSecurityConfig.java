package com.example.demo.security;


import com.example.demo.security.filters.JwtTokenFilter;
import com.example.demo.user.Role;
import com.example.demo.user.UserRepo;
import com.example.demo.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.servlet.http.HttpServletResponse;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Profile("ldapauth")
@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
@EnableWebMvc
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
//    private final UserService userService;
    private final JwtTokenFilter jwtTokenFilter;
//    private final LdapUserAuthoritiesPopulator ldapUserAuthoritiesPopulator;

    @Override
    protected void configure(HttpSecurity http)throws Exception{
        http = http.cors().and().csrf().disable();
        http = http.sessionManagement().sessionCreationPolicy(STATELESS).and();
        http = http.authorizeRequests().antMatchers("/api/v1/public/login/**").permitAll().and();
        http = http.exceptionHandling().authenticationEntryPoint(((request, response, authException) ->
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage()))).and();
        http.authorizeRequests()
//                 Our public endpoints
//                .antMatchers("/api/v1/public/**").permitAll().
//                antMatchers( "/api/v1/data/**").hasRole(Role.ROLE_USER)
//                .antMatchers("/api/v1/admin/**").hasRole(Role.ROLE_ADMIN)
                // Our private endpoints
                .anyRequest().fullyAuthenticated().and().formLogin();

//         Add JWT token filter
        http.addFilterBefore(
                jwtTokenFilter,
                UsernamePasswordAuthenticationFilter.class
        );
    }
    @Autowired
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.userDetailsService(username -> userService.loadUserByUsername(username));
        auth

                .ldapAuthentication()
                .userDnPatterns("uid={0},ou=people")
                .groupSearchBase("ou=groups")
                .contextSource()
                .url("ldap://localhost:8389/dc=springframework,dc=org")
                .and()
                .passwordCompare()
                .passwordEncoder(new BCryptPasswordEncoder())
                .passwordAttribute("userPassword").and()
                .userSearchFilter("sAMAccountName={0}");
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {

        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Used by spring security if CORS is enabled.
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

}