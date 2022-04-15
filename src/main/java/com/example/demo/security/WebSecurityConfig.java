package com.example.demo.security;


import com.example.demo.security.filter.CustomAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.ldap.authentication.UserDetailsServiceLdapAuthoritiesPopulator;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletResponse;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
@Order(1)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    private final UserDetailsService userDetailsService;
    @Override
    protected void configure(HttpSecurity http)throws Exception{
        http.csrf().disable();
        http.cors().disable();
        http.sessionManagement().sessionCreationPolicy(STATELESS);
        http = http
                .exceptionHandling()
                .authenticationEntryPoint(
                        (request, response, ex) -> {
                            response.sendError(
                                    HttpServletResponse.SC_UNAUTHORIZED,
                                    ex.getMessage()
                            );
                        }
                )
                .and();

//        http.authorizeRequests().antMatchers(HttpMethod.GET, "/users/**");
//        http.antMatcher("/users/**").authorizeRequests().anyRequest().fullyAuthenticated();
        http.authorizeRequests()
                .anyRequest().authenticated().and();

        http.addFilterAfter(new CustomAuthenticationFilter(authenticationManagerBean()), UsernamePasswordAuthenticationFilter.class);
    }
    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
                auth.ldapAuthentication()
                .userDnPatterns("uid={0},ou=people")
                .groupSearchBase("ou=groups")
                .contextSource()
                .url("ldap://localhost:8389/dc=springframework,dc=org")
                .and()
                .passwordCompare()
                .passwordEncoder(new BCryptPasswordEncoder())
                .passwordAttribute("userPassword")
                        .and().ldapAuthoritiesPopulator(new UserDetailsServiceLdapAuthoritiesPopulator(userDetailsService));
    }
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception{
        return super.authenticationManagerBean();
    }
}