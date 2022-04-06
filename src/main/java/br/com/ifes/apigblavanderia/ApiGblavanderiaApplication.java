package br.com.ifes.apigblavanderia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class ApiGblavanderiaApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGblavanderiaApplication.class, args);
    }

}
