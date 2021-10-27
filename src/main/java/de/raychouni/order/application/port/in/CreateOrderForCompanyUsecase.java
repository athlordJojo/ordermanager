package de.raychouni.order.application.port.in;

import de.raychouni.order.domain.Order;

public interface CreateOrderForCompanyUsecase{

    Order createOrder(CreateOrderForCompanyCommand createOrderForCompanyCommand);
}
