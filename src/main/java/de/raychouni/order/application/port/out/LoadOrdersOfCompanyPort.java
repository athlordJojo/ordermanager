package de.raychouni.order.application.port.out;

import de.raychouni.order.domain.Order;

import java.util.List;
import java.util.UUID;

public interface LoadOrdersOfCompanyPort {
    List<Order> loadOrdersOfCompany(UUID companyUuid);
}
