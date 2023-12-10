package com.ilisi.mstxfleetdbspostgis.kafkaConsumer;

import com.ilisi.mstxfleetdbspostgis.model.UserLocation;
import com.ilisi.mstxfleetdbspostgis.repository.UserLocationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class LocationConsumer {

    private final UserLocationRepository userLocationRepository;

    @KafkaListener(topics = "location", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(ConsumerRecord<String, Map<String, Object>> record) {
        try {
            Map<String, Object> message = record.value();
            log.info("Message received from Kafka topic location: {}", message);

            UserLocation userLocation = userLocationRepository.findByUserId(message.get("userId").toString())
                    .orElse(UserLocation.builder()
                            .userId(message.get("userId").toString())
                            .createdAt(Instant.parse(message.get("createdAt").toString()))
                            .build());

            userLocation.setOnline(message.get("isOnline").toString().equals("true"));
            if (userLocation.isOnline()) {
                userLocation.setUserType(message.get("userType").toString());
                userLocation.setLocation(message.get("location").toString());
                userLocation.setUpdatedAt(message.get("updatedAt").toString().isEmpty() ? null : Instant.parse(message.get("updatedAt").toString()));
            }

            userLocationRepository.save(userLocation);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}