package cl.duoc.Inventory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

// Indica que esta clase es la aplicacion principal de Spring Boot
   
@SpringBootApplication
@EnableDiscoveryClient
public class InventoryApplication {

    // Metodo main que inicia el microservicio de inventario
    public static void main(String[] args) {
        SpringApplication.run(InventoryApplication.class, args);
    }
}
