package com.example.demo.security;


import com.example.demo.security.filter.CustomAuthenticationFilter;
import com.example.demo.security.filter.CustomAuthorizationFilter;
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
//the websecurity config is where the authentication happens here we're making two types of authentication
// we're attempting to authenticate the user in the ldap server and in our database at the same time
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    private final UserDetailsService userDetailsService;
    private final JWTUtility jwtUtility;
    //in this method we put our http configuration and securing our routes
    //we add our filters here too
    @Override
    protected void configure(HttpSecurity http)throws Exception{
        CustomAuthenticationFilter customAuthenticationFilter = new CustomAuthenticationFilter(authenticationManagerBean(), jwtUtility);
        customAuthenticationFilter.setFilterProcessesUrl("/api/v1/login");
        http.csrf().disable();
        http.cors().disable();
        http.sessionManagement().sessionCreationPolicy(STATELESS);
        http.authorizeRequests().antMatchers("/api/v1/login/**", "/api/v1/token/refresh/**" ).permitAll();
//        http = http
//                .exceptionHandling()
//                .authenticationEntryPoint(
//                        (request, response, ex) -> {
//                            response.sendError(
//                                    HttpServletResponse.SC_UNAUTHORIZED,
//                                    ex.getMessage()
//                            );
//                        }
//                )
//                .and();

//        http.authorizeRequests().antMatchers(HttpMethod.GET, "/users/**");
//        http.antMatcher("/users/**").authorizeRequests().anyRequest().fullyAuthenticated();
        http.authorizeRequests()
                .anyRequest().authenticated().and();
        http.addFilter(customAuthenticationFilter) ;
        http.addFilterBefore(new CustomAuthorizationFilter(jwtUtility), UsernamePasswordAuthenticationFilter.class);
    }
    //in this method we apply the ldap authentication logic and tell it to check the credentials in the database at the same time
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