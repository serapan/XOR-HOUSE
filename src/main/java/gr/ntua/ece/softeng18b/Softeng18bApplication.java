package gr.ntua.ece.softeng18b;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.filter.CommonsRequestLoggingFilter;


@SpringBootApplication
public class Softeng18bApplication {

    public static void main(String[] args) {
        SpringApplication.run(Softeng18bApplication.class, args);
    }


}
