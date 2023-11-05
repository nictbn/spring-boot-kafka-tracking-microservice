package com.example.tracking.handler;

import com.example.message.DispatchPreparing;
import com.example.tracking.service.TrackingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.example.tracking.util.TestEventData.buildDispatchPreparingEvent;
import static java.util.UUID.randomUUID;
import static org.mockito.Mockito.*;

class TrackingHandlerTest {
    TrackingHandler trackingHandler;
    TrackingService trackingServiceMock;

    @BeforeEach
    void setUp() {
        trackingServiceMock = mock(TrackingService.class);
        trackingHandler = new TrackingHandler(trackingServiceMock);
    }

    @Test
    void processSuccessful() throws Exception {
        String key = randomUUID().toString();
        DispatchPreparing event = buildDispatchPreparingEvent(randomUUID());
        trackingHandler.listen(0, key, event);
        verify(trackingServiceMock, times(1)).process(key, event);
    }

    @Test
    void processException() throws Exception {
        String key = randomUUID().toString();
        DispatchPreparing testEvent = buildDispatchPreparingEvent(randomUUID());
        doThrow(new RuntimeException("Service failure")).when(trackingServiceMock).process(key, testEvent);
        trackingHandler.listen(0, key, testEvent);
        verify(trackingServiceMock, times(1)).process(key, testEvent);
    }
}