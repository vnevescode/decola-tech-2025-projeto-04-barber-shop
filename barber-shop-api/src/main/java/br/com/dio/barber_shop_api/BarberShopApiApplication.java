package br.com.dio.barber_shop_api;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BarberShopApiApplication {

	public static void main(String[] args) {

		String env = System.getenv("ENVIRONMENT");
		if (env == null || env.isBlank()) {
			env = System.getProperty("ENVIRONMENT", "dev"); // fallback para dev
		}

		Dotenv dotenv = Dotenv.configure()
				.filename(".env." + env)
				.ignoreIfMissing()
				.load();

		// Seta como variáveis do sistema para o Spring usar
		System.setProperty("SPRING_PROFILES_ACTIVE", dotenv.get("SPRING_PROFILES_ACTIVE"));
		System.setProperty("DB_URL", dotenv.get("DB_URL"));
		System.setProperty("DB_USER", dotenv.get("DB_USER"));
		System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));

		SpringApplication.run(BarberShopApiApplication.class, args);
	}

}
