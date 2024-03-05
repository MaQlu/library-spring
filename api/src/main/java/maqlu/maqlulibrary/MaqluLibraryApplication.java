package maqlu.maqlulibrary;

import maqlu.maqlulibrary.utilities.FillDB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class MaqluLibraryApplication implements WebMvcConfigurer {
	public static void main(String[] args) {
		SpringApplication.run(MaqluLibraryApplication.class, args);
	}

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**")
      .allowedOrigins("http://localhost:3000", "http://192.168.100.16:3000", "http://87.205.117.151:3000")
      .allowedMethods("GET", "POST", "PUT", "DELETE")
      .allowedHeaders("Authorization")
      .exposedHeaders("Authorization")
      .allowCredentials(true)
      .maxAge(3600);;
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
