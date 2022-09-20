package com.example.blogsearchserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BlogSearchServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(BlogSearchServerApplication.class, args);
    }

}
