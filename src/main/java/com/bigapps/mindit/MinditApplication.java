package com.bigapps.mindit;

import io.sentry.Sentry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MinditApplication {

	public static void main(String[] args) {
		SpringApplication.run(MinditApplication.class, args);
        Sentry.init();
        }

}
