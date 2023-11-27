package com.ilisi.mstxfleetdbspostgis.kafkalistner;

import com.ilisi.mstxfleetdbspostgis.model.UserLocation;
import com.ilisi.mstxfleetdbspostgis.repository.UserLocationRepository;
import com.ilisi.mstxfleetlocation.entity.LocationMessage;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaConsumer {

    private final UserLocationRepository userLocationRepository;

    @KafkaListener(topics = "location", groupId = "{spring.kafka.consumer.group-id}")
    public void consume(ConsumerRecord<String, LocationMessage> record) {
        try {
            LocationMessage message = record.value();
            System.out.println("-------------------");
            System.out.println("Consumed message : " + message.toString());
            String point = String.format("POINT(%s %s)", message.getLongitude(), message.getLatitude());
            UserLocation userLocation = UserLocation.builder()
                    .userId(message.getUserId())
                    .location(point)
                    .userType(message.getUserType())
                    .isOnline(true)
                    .build();
            userLocationRepository.save(userLocation);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}