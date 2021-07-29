package de.raychouni.order.application.port.out;

import de.raychouni.order.domain.Order;

import java.util.UUID;

public interface DeleteOrderOfCompanyPort {
    Order deleteOrderOfCompany(UUID companyId, UUID orderId);
}
