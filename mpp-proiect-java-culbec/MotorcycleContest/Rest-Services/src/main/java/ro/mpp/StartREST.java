package ro.mpp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"Hibernate", "ro.mpp.Controllers"})
public class StartREST {
    public static void main (String[] args) {
        SpringApplication.run(StartREST.class, args);
    }
}
