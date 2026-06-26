package cl.duoc.Perfulandiashipping;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class PerfulandiashippingApplication {

	public static void main(String[] args) {
		SpringApplication.run(PerfulandiashippingApplication.class, args);
	}

}
