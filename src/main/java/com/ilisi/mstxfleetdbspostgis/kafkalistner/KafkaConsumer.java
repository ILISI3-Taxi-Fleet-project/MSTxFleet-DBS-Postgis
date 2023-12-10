package com.ilisi.mstxfleetdbspostgis.kafkalistner;

import com.ilisi.mstxfleetdbspostgis.model.UserLocation;
import com.ilisi.mstxfleetdbspostgis.repository.UserLocationRepository;
import com.ilisi.mstxfleetlocation.entity.LocationMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumer {

    private final UserLocationRepository userLocationRepository;

    @KafkaListener(topics = "location", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(ConsumerRecord<String, LocationMessage> record) {
        try {
            LocationMessage message = record.value();
            log.info("Message received from Kafka topic location: {}", message);

            UserLocation userLocation = userLocationRepository.findByUserId(message.getUserId())
                    .orElse(UserLocation.builder()
                            .userId(message.getUserId())
                            .createdAt(message.getCreatedAt())
                            .build());

            userLocation.setOnline(message.getIsOnline());
            if(message.getIsOnline()) {
                userLocation.setUserType(message.getUserType());
                userLocation.setLocation(message.getLocation());
                userLocation.setUpdatedAt(message.getUpdatedAt());
            }

            userLocationRepository.save(userLocation);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}