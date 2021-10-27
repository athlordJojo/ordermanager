package de.raychouni.order.adapter.out.websocket;

import de.raychouni.order.domain.OrderUpdate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import static de.raychouni.order.domain.OrderUpdate.CHANGE_TYPE.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OrderNotificationAdapterTest {

    @Mock
    SimpMessagingTemplate simpMessagingTemplate;
    @Mock
    ApplicationEventPublisher eventPublisher;

    private OrderNotificationAdapter orderNotificationAdapter;

    @BeforeEach
    void setUp() {
        orderNotificationAdapter = new OrderNotificationAdapter(simpMessagingTemplate, eventPublisher);
    }

    @Test
    void sendOrderChangedMessage() {
        ArgumentCaptor<OrderUpdate> argument = ArgumentCaptor.forClass(OrderUpdate.class);
        orderNotificationAdapter.sendOrderChangedMessage(DELETED);
        verify(eventPublisher).publishEvent(argument.capture());
        OrderUpdate passedOrderUpdate = argument.getValue();
        assertEquals(DELETED, passedOrderUpdate.getChange());
    }

    @Test
    void onEntityUpdate() {
        OrderUpdate orderUpdate = new OrderUpdate(DELETED);
        ArgumentCaptor<OrderUpdate> orderCapture = ArgumentCaptor.forClass(OrderUpdate.class);
        ArgumentCaptor<String> topicCapture = ArgumentCaptor.forClass(String.class);

        orderNotificationAdapter.onEntityUpdate(orderUpdate);

        verify(simpMessagingTemplate).convertAndSend(topicCapture.capture(), orderCapture.capture());

        OrderUpdate capturedOrder = orderCapture.getValue();
        assertEquals(orderUpdate, capturedOrder);
        String capturedTopic = topicCapture.getValue();
        assertEquals("/topic/orders", capturedTopic);
    }
}
