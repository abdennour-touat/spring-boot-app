package com.example.demo;

import com.example.demo.user.AppUser;
import com.example.demo.user.Role;
import com.example.demo.user.UserRepo;
import com.example.demo.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;


@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class DemoApplication {
	private static final Logger log = LoggerFactory.getLogger(DemoApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Bean
	public CommandLineRunner demo(UserService userService) {
		return (args) -> {

			userService.saveRole(new Role(null,"admin"));
			userService.saveRole(new Role(null,"super_admin"));
			userService.saveRole(new Role(null,"watcher"));


//			// save a few customers
//			repository.save(new AppUser("Jack", "Bauer"));
//			repository.save(new AppUser("Chloe", "O'Brian"));
//			repository.save(new AppUser("Kim", "Bauer"));
//			repository.save(new AppUser("David", "Palmer"));
//
//			// fetch all customers
//			log.info("Customers found with findAll():");
//			log.info("-------------------------------");
//			for (AppUser user : repository.findAll()) {
//				log.info(user.toString());
//			}
//			log.info("");

//			// fetch an individual customer by ID
//			Customer customer = repository.findById(1L);
//			log.info("Customer found with findById(1L):");
//			log.info("--------------------------------");
//			log.info(customer.toString());
//			log.info("");
//
//			// fetch customers by last name
//			log.info("Customer found with findByLastName('Bauer'):");
//			log.info("--------------------------------------------");
//			repository.findByLastName("Bauer").forEach(bauer -> {
//				log.info(bauer.toString());
//			});
//			// for (Customer bauer : repository.findByLastName("Bauer")) {
//			//  log.info(bauer.toString());
//			// }
//			log.info("");
		};
	}

}
