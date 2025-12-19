package co.java.app.contanotify;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ContanotifyApplication {

	public static void main(String[] args) {
		SpringApplication.run(ContanotifyApplication.class, args);
	}

}
