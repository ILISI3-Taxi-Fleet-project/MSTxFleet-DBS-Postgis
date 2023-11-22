package com.ilisi.mstxfleetdbspostgis;

import com.ilisi.mstxfleetdbspostgis.repository.UserLocationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class MsTxFleetDbsPostgisApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsTxFleetDbsPostgisApplication.class, args);
    }
//    @Bean
    CommandLineRunner commandLineRunner(UserLocationRepository userLocationRepository) {
        return args -> {

            log.info(userLocationRepository.findNearbyOnlineUsers("POINT(-7.5790 33.5521)", 1000).toString());
        };
    }
}

