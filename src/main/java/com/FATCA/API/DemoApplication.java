package com.FATCA.API;

import com.FATCA.API.fileStorage.FilesStorageService;
import com.FATCA.API.fileStorage.FilesStorageServiceImpl;
import com.FATCA.API.history.History;
import com.FATCA.API.history.HistoryService;
import com.FATCA.API.user.AppUser;
import com.FATCA.API.user.Roles;
import com.FATCA.API.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


/*
* The main function to run the application
* */
//TODO : handle all the exception
@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
@Configuration
public class DemoApplication {
	@Bean
	FilesStorageService filesStorageService(){
		return  new FilesStorageServiceImpl();
	}
	private static final Logger log = LoggerFactory.getLogger(DemoApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**")
						.allowedOrigins("*")
						.allowedMethods("GET", "PUT", "POST", "PATCH", "DELETE", "OPTIONS");
			}
		};
	}
	@Bean
	public CommandLineRunner demo(UserService userService, FilesStorageService storageService) {
		return (args) -> {
			storageService.deleteAll();
			storageService.init();
		userService.saveUser(new AppUser("abdenour","abdou" , "ROLE_AUDITOR"));
			userService.saveUser(new AppUser("badr","aissa" , "ROLE_AUDITOR"));
			userService.saveUser(new AppUser("mohamed","moh" , "ROLE_AUDITOR"));
			userService.saveUser(new AppUser("ackerman","mikasa" , "ROLE_AUDITOR"));
			userService.saveUser(new AppUser("ben", "ben", "ROLE_ADMIN"));
			userService.saveUser(new AppUser("aissa", "saad eddine", "ROLE_ADMIN"));
			userService.saveUser(new AppUser("mohamed3", "djamel", "ROLE_ADMIN"));
			userService.saveUser(new AppUser("joe1", "alaa", "ROLE_AUDITOR"));
			userService.saveUser(new AppUser("joe2", "ratiba", "ROLE_EDITOR"));
			userService.saveUser(new AppUser("joe3", "karim", "ROLE_EDITOR"));
			userService.saveUser(new AppUser("joe4", "manel", "ROLE_EDITOR"));
			userService.saveUser(new AppUser("joe5", "samir", "ROLE_EDITOR"));

//			userService.addRole("mikasa", String.valueOf(Roles.ROLE_EDITOR));
//			userService.addRole("aissa", String.valueOf(Roles.ROLE_EDITOR));
//			userService.addRole("ben", String.valueOf(Roles.ROLE_EDITOR));
//			userService.addRole("moh", String.valueOf(Roles.ROLE_EDITOR));
//			userService.addRole("djamel", String.valueOf(Roles.ROLE_EDITOR));
//			userService.addRole("alaa", String.valueOf(Roles.ROLE_EDITOR));
//			userService.addRole("abdou", String.valueOf(Roles.ROLE_ADMIN));
//			userService.addRole("abdou", String.valueOf(Roles.ROLE_EDITOR));
//			userService.addRole("saad eddine", String.valueOf(Roles.ROLE_AUDITOR));
//			userService.addRole("alaa", String.valueOf(Roles.ROLE_EDITOR));
//			userService.addRole("ratiba", String.valueOf(Roles.ROLE_AUDITOR));
//			userService.addRole("karim", String.valueOf(Roles.ROLE_ADMIN));
//			userService.addRole("manel", String.valueOf(Roles.ROLE_AUDITOR));
//			userService.addRole("samir", String.valueOf(Roles.ROLE_EDITOR));
//			historyService.saveHistory(new History("",userService.getUser("saad eddine")));

		};
	}

}
