package com.example.demo;

import ch.qos.logback.core.encoder.EchoEncoder;
import com.example.demo.security.filters.JwtTokenFilter;
import com.example.demo.user.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.ldap.core.ContextSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;
import org.springframework.security.ldap.server.UnboundIdContainer;


@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class DemoApplication {
	private static final Logger log = LoggerFactory.getLogger(DemoApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	AuthenticationManager authenticationManager(){
		return new AuthenticationManager() {
			@Override
			public Authentication authenticate(Authentication authentication) throws AuthenticationException {
				return null;
			}
		};
	}

	@Bean
	public CommandLineRunner demo(UserService userService) {
		return (args) -> {

			userService.saveUser(new AppUser("abdenour","abdou" , "password"));
			userService.saveUser(new AppUser("badr","aissa" , "aissa"));
			userService.saveUser(new AppUser("mohamed","moh" , "password"));
			userService.saveUser(new AppUser("ackerman","mikasa" , "password"));
//			userService.saveUser(new AppUser("ben", "ben", "benpassword"));

			userService.addRole("abdou", String.valueOf(Role.ROLE_ADMIN));
			userService.addRole("abdou", String.valueOf(Role.ROLE_EDITOR));
			userService.addRole("aissa", String.valueOf(Role.ROLE_USER));

		};
	}

}
