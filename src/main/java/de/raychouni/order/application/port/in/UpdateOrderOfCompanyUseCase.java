package de.raychouni.order.application.port.in;

import de.raychouni.order.domain.Order;

public interface UpdateOrderOfCompanyUseCase {
    Order updateOrder(UpdateOrderOfCompanyCommand updateOrderOfCompanyCommand);
}
