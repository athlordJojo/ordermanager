package de.raychouni.order.application.port.out;

import de.raychouni.order.domain.Order;

public interface UpdateOrderOfCompanyPort {
    Order updateOrderOfCompany(Order order);
}
