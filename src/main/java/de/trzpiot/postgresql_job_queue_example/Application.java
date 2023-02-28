package de.trzpiot.postgresql_job_queue_example;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import static org.springframework.boot.SpringApplication.run;

@EnableAsync
@EnableScheduling
@SpringBootApplication
public class Application {

    public static void main(final String... args) {
        run(Application.class, args);
    }
}
