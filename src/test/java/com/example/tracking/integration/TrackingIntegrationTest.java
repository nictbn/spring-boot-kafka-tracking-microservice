package com.example.tracking.integration;

import com.example.tracking.TrackingConfiguration;
import com.example.message.DispatchPreparing;
import com.example.message.TrackingStatusUpdated;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static com.example.tracking.util.TestEventData.buildDispatchPreparingEvent;
import static java.util.UUID.randomUUID;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.equalTo;

@Slf4j
@SpringBootTest(classes = {TrackingConfiguration.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@ActiveProfiles("test")
@EmbeddedKafka(controlledShutdown = true)
public class TrackingIntegrationTest {
    private static final String DISPATCH_TRACKING_TOPIC = "dispatch.tracking";

    public static final String TRACKING_STATUS_TOPIC = "tracking.status";

    @Autowired
    private KafkaTemplate kafkaTemplate;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    private KafkaListenerEndpointRegistry registry;

    @Autowired
    private KafkaTestListener testListener;

    @Configuration
    static class TestConfig {
        @Bean
        public KafkaTestListener testListener() {
            return new KafkaTestListener();
        }
    }

    @KafkaListener(groupId = "kafkaIntegrationTest", topics = TRACKING_STATUS_TOPIC)
    public static class KafkaTestListener {
        AtomicInteger trackingStatusMessageCounter = new AtomicInteger(0);

        @KafkaHandler
        void receiveTrackingStatusMessage(@Payload TrackingStatusUpdated payload) {
            log.debug("Received TrackingStatus: " + payload);
            trackingStatusMessageCounter.incrementAndGet();
        }
    }

    @BeforeEach
    public void setUp() {
        testListener.trackingStatusMessageCounter.set(0);
        registry.getListenerContainers().stream().forEach(container ->
                ContainerTestUtils.waitForAssignment(container, embeddedKafkaBroker.getPartitionsPerTopic()));
    }

    @Test
    public void testFlow() throws Exception {
        UUID orderId = randomUUID();
        DispatchPreparing dispatchPreparing = buildDispatchPreparingEvent(orderId);
        sendMessage(DISPATCH_TRACKING_TOPIC, orderId.toString(), dispatchPreparing);
        await()
                .atMost(3, TimeUnit.SECONDS)
                .pollDelay(100, TimeUnit.MILLISECONDS)
                .until(testListener.trackingStatusMessageCounter::get, equalTo(1));
    }

    private void sendMessage(String topic, String key, Object data) throws Exception {
        kafkaTemplate.send(MessageBuilder
                .withPayload(data)
                .setHeader(KafkaHeaders.KEY, key)
                .setHeader(KafkaHeaders.TOPIC, topic)
                .build()).get();
    }
}
