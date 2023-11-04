package com.example.tracking.handler;

import com.example.tracking.service.TrackingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class TrackingHandlerTest {
    public static final String TEST_STRING = "Test";
    TrackingHandler trackingHandler;
    TrackingService trackingServiceMock;

    @BeforeEach
    void setUp() {
        trackingServiceMock = mock(TrackingService.class);
        trackingHandler = new TrackingHandler(trackingServiceMock);
    }

    @Test
    void processSuccessful() {
        trackingHandler.listen(TEST_STRING);
        verify(trackingServiceMock, times(1)).process(TEST_STRING);
    }
}