package br.com.libertadfacilites;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class MailServiceApplication {

	public static void main(String[] args) {

		Dotenv.configure().ignoreIfMissing().load();
		SpringApplication.run(MailServiceApplication.class, args);
	}

}
