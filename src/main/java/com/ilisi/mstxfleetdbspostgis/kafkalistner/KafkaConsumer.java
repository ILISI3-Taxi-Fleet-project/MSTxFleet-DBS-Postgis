package com.ilisi.mstxfleetdbspostgis.kafkalistner;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {
    @KafkaListener(topics = "location", groupId = "{spring.kafka.consumer.group-id}")
    public void consume(ConsumerRecord<String, Object> record) {
        System.out.println("-------------------");
        System.out.println("Consumed message: " + record.value());
        System.out.println("-------------------");
    }

}
