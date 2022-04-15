package de.raychouni.order.application.port.in;

import de.raychouni.order.domain.Order;
import java.util.List;

public interface GetAllOrdersForCompanyUseCase {
    List<Order> getAllOrdersByCompanyId(GetAllOrdersForCompanyCommand getAllOrdersForCompanyCommand);
}
