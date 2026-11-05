package ru.cohenrol.objectivesandkeyresults;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ObjectivesAndKeyResultsApplication {

    public static void main(String[] args) {
        SpringApplication.run(ObjectivesAndKeyResultsApplication.class, args);
    }

}
