package dev.siraj.restauron;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class RestauronApplication {

	public static void main(String[] args) {
		SpringApplication.run(RestauronApplication.class, args);
	}

}
