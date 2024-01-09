package maqlu.maqlulibrary;

import maqlu.maqlulibrary.utilities.FillDB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class MaqluLibraryApplication {
	public static void main(String[] args) {
		SpringApplication.run(MaqluLibraryApplication.class, args);
	}

	@Autowired
	FillDB fillDB;

	@Bean
	CommandLineRunner runner(){
		return args -> {
			fillDB.FillDB();
		};
	}

}
