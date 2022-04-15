package de.raychouni.order.application.port.out;

import de.raychouni.order.domain.Order;
import java.util.UUID;

public interface LoadOrderOfCompanyPort {
    Order loadOrderOfCompany(UUID companyId, UUID orderId);
}
