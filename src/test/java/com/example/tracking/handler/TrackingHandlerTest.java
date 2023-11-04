package com.example.tracking.handler;

import com.example.tracking.message.DispatchPreparing;
import com.example.tracking.service.TrackingService;
import com.example.tracking.util.TestEventData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static com.example.tracking.util.TestEventData.buildDispatchPreparingEvent;
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
        DispatchPreparing event = buildDispatchPreparingEvent(UUID.randomUUID());
        trackingHandler.listen(event);
        verify(trackingServiceMock, times(1)).process(event);
    }

    @Test
    void processException() throws Exception {
        DispatchPreparing testEvent = buildDispatchPreparingEvent(UUID.randomUUID());
        doThrow(new RuntimeException("Service failure")).when(trackingServiceMock).process(testEvent);
        trackingHandler.listen(testEvent);
        verify(trackingServiceMock, times(1)).process(testEvent);
    }
}