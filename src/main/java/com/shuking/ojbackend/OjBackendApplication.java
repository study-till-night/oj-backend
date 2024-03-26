package com.shuking.ojbackend;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableKnife4j
public class OjBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(OjBackendApplication.class, args);
    }

}
