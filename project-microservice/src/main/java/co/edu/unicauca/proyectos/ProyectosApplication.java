package co.edu.unicauca.proyectos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "co.edu.unicauca.proyectos.services.clients")
public class ProyectosApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProyectosApplication.class, args);
	}

}
