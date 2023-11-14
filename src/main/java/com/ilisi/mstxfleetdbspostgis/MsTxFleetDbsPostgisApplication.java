package com.ilisi.mstxfleetdbspostgis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.support.converter.StringJsonMessageConverter;

@EnableKafka
@SpringBootApplication
public class MsTxFleetDbsPostgisApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsTxFleetDbsPostgisApplication.class, args);
    }

    @Bean
    public StringJsonMessageConverter stringJsonMessageConverter() {
        return new StringJsonMessageConverter();
    }
}

