package com.voice.separation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories
public class VoiceSeparationApplication {

    public static void main(String[] args) {
        SpringApplication.run(VoiceSeparationApplication.class, args);
    }
}
