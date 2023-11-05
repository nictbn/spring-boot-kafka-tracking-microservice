package com.example.tracking.service;

import com.example.message.DispatchPreparing;
import com.example.message.TrackingStatusUpdated;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.concurrent.CompletableFuture;

import static com.example.tracking.service.TrackingService.TRACKING_STATUS_TOPIC;
import static com.example.tracking.util.TestEventData.buildDispatchPreparingEvent;
import static java.util.UUID.randomUUID;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;


class TrackingServiceTest {
    private KafkaTemplate kafkaProducerMock;
    private TrackingService service;

    @BeforeEach
    void setUp() {
        kafkaProducerMock = mock(KafkaTemplate.class);
        service = new TrackingService(kafkaProducerMock);
    }

    @Test
    void processSuccessful() throws Exception {
        String key = randomUUID().toString();
        when(kafkaProducerMock.send(anyString(), anyString(), any(TrackingStatusUpdated.class))).thenReturn(mock(CompletableFuture.class));
        DispatchPreparing testEvent = buildDispatchPreparingEvent(randomUUID());
        service.process(key, testEvent);
        verify(kafkaProducerMock, times(1)).send(eq("tracking.status"), eq(key), any(TrackingStatusUpdated.class));
    }

    @Test
    void processException() {
        String key = randomUUID().toString();
        doThrow(new RuntimeException("dispatch preparing producer failure")).when(kafkaProducerMock).send(eq("tracking.status"), anyString(),  any(TrackingStatusUpdated.class));
        DispatchPreparing testEvent = buildDispatchPreparingEvent(randomUUID());
        Exception exception = assertThrows(RuntimeException.class, () -> service.process(key, testEvent));
        verify(kafkaProducerMock, times(1)).send(eq(TRACKING_STATUS_TOPIC), eq(key), any(TrackingStatusUpdated.class));
        assertThat(exception.getMessage(), equalTo("dispatch preparing producer failure"));
    }
}