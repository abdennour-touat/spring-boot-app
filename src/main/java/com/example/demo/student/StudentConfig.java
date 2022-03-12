package com.example.demo.student;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

@Configuration
public class StudentConfig {
    @Bean
    CommandLineRunner commandLineRunner(StudentRepository repository){
        return args -> {
            Student lysa = new Student(
                    "lysa",
                    "lysa@gmail.com",
                    LocalDate.of(2001, Month.DECEMBER, 3)
            );
            Student abdenour = new Student(
                    "abdenour",
                    "abdenour@gmail.com",
                    LocalDate.of(2001, Month.AUGUST, 30)
            );
            repository.saveAll(List.of(lysa, abdenour));
        };
    }
}
