package de.raychouni.order.application.port.in;

import de.raychouni.order.adapter.out.persistence.entities.Order;

import java.util.List;

public interface GetAllOrdersForCompanyUseCase {
    List<Order> getAllOrdersByCompanyId(GetAllOrdersForCompanyCommand getAllOrdersForCompanyCommand);
}
