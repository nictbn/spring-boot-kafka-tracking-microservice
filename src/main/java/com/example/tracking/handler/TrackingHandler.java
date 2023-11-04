package com.example.tracking.handler;

import com.example.tracking.service.TrackingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class TrackingHandler {
    private final TrackingService trackingService;
    @KafkaListener(
            id = "trackingConsumerClient",
            topics = "dispatch.tracking",
            groupId = "dispatch.tracking.consumer"
    )
    public void listen(String payload) {
        log.info("Received message payload: " + payload);
        trackingService.process(payload);
    }
}
