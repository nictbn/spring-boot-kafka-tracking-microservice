package com.example.tracking.service;

import com.example.message.DispatchPreparing;
import com.example.message.TrackingStatus;
import com.example.message.TrackingStatusUpdated;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TrackingService {
    public static final String TRACKING_STATUS_TOPIC = "tracking.status";

    private final KafkaTemplate<String, Object> kafkaTemplate;
    public void process(DispatchPreparing dispatchPreparing) throws Exception {
        TrackingStatusUpdated trackingStatusUpdated = TrackingStatusUpdated.builder()
                .orderId(dispatchPreparing.getOrderId())
                .status(TrackingStatus.PREPARING)
                .build();
        kafkaTemplate.send(TRACKING_STATUS_TOPIC, trackingStatusUpdated).get();
    }
}
