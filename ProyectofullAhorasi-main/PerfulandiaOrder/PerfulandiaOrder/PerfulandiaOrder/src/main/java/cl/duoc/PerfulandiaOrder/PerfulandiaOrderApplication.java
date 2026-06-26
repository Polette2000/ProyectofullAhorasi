package cl.duoc.PerfulandiaOrder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class PerfulandiaOrderApplication {

	public static void main(String[] args) {
		SpringApplication.run(PerfulandiaOrderApplication.class, args);
	}

}
