package br.com.libertadfacilities.blog;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class BlogApplication {

	public static void main(String[] args) {

		Dotenv.configure().ignoreIfMissing().load();
		SpringApplication.run(BlogApplication.class, args);
	}

}
