import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"Repository", "Controllers"})
public class StartREST {
    public static void main(String[] args) {
        SpringApplication.run(StartREST.class, args);
    }
}
