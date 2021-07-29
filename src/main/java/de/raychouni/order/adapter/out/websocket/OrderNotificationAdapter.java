package de.raychouni.order.adapter.out.websocket;

import de.raychouni.order.application.port.out.OrderChangedPort;
import de.raychouni.order.domain.OrderUpdate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
public class OrderNotificationAdapter implements OrderChangedPort {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ApplicationEventPublisher eventPublisher;

    public OrderNotificationAdapter(SimpMessagingTemplate simpMessagingTemplate, ApplicationEventPublisher eventPublisher) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void sendOrderChangedMessage(OrderUpdate.CHANGE_TYPE updateType) {
        eventPublisher.publishEvent(new OrderUpdate(updateType));
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onEntityUpdate(final OrderUpdate orderUpdate) {
        log.debug("Sending order update");
        simpMessagingTemplate.convertAndSend("/topic/orders", orderUpdate);
    }
}
