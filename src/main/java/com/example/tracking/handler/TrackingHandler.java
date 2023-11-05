package com.example.tracking.handler;

import com.example.message.DispatchCompleted;
import com.example.message.DispatchPreparing;
import com.example.tracking.service.TrackingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
@KafkaListener(
        id = "trackingConsumerClient",
        topics = "dispatch.tracking",
        groupId = "dispatch.tracking.consumer",
        containerFactory = "kafkaListenerContainerFactory"
)
public class TrackingHandler {
    private final TrackingService trackingService;
    @KafkaHandler
    public void listen(
            @Header(KafkaHeaders.RECEIVED_PARTITION) Integer partition,
            @Header(KafkaHeaders.RECEIVED_KEY) String key,
            @Payload DispatchPreparing payload
    ) {
        log.info("Received dispatch preparing message: partition: " + partition + " key:" + key + " payload: " + payload);
        try {
            trackingService.process(key, payload);
        } catch (Exception e) {
            log.error("Processing failure", e);
        }
    }

    @KafkaHandler
    public void listen(
            @Header(KafkaHeaders.RECEIVED_PARTITION) Integer partition,
            @Header(KafkaHeaders.RECEIVED_KEY) String key,
            @Payload DispatchCompleted payload
    ) {
        log.info("Received dispatch completed message: partition: " + partition + " key:" + key + " payload: " + payload);
        try {
            trackingService.process(key, payload);
        } catch (Exception e) {
            log.error("Processing failure", e);
        }
    }
}
