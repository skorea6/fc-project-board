package fc.projectboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan
@SpringBootApplication
public class FcProjectBoardApplication {

    public static void main(String[] args) {
        SpringApplication.run(FcProjectBoardApplication.class, args);
    }

}
