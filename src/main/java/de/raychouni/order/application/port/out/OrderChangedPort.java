package de.raychouni.order.application.port.out;

import de.raychouni.order.domain.OrderUpdate;

public interface OrderChangedPort {
    void sendOrderChangedMessage(OrderUpdate.CHANGE_TYPE updateType);
}
