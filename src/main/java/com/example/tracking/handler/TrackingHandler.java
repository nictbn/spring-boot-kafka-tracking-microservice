package com.example.tracking.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TrackingHandler {
    @KafkaListener(
            id = "trackingConsumerClient",
            topics = "dispatch.tracking",
            groupId = "dispatch.tracking.consumer"
    )
    public void process(String payload) {
        log.info("Received message payload: " + payload);
    }
}
