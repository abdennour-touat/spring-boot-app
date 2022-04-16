package com.example.demo;

import com.example.demo.user.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
/*
* The main function tot run the application
* */
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
	public CommandLineRunner demo(UserService userService) {
		return (args) -> {

			userService.saveUser(new AppUser("abdenour","abdou" , "password"));
			userService.saveUser(new AppUser("badr","aissa" , "aissa"));
			userService.saveUser(new AppUser("mohamed","moh" , "password"));
			userService.saveUser(new AppUser("ackerman","mikasa" , "password"));
			userService.saveUser(new AppUser("ben", "ben", "benpassword"));
			userService.saveUser(new AppUser("joe", "joe", "joespassword"));


			userService.addRole("abdou", String.valueOf(Roles.ROLE_ADMIN));
			userService.addRole("abdou", String.valueOf(Roles.ROLE_EDITOR));
			userService.addRole("aissa", String.valueOf(Roles.ROLE_USER));

		};
	}

}
