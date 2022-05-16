package kz.iitu.orken.medical_managament_system;

import kz.iitu.orken.medical_managament_system.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableJpaAuditing
public class MedicalManagamentSystemApplication {
    private static ApplicationContext applicationContext;

    public static void main(String[] args) {
        applicationContext = SpringApplication.run(MedicalManagamentSystemApplication.class, args);
        UserService userService = applicationContext.getBean("userServiceImpl", UserService.class);
        userService.findAll();
    }

}
