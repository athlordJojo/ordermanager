package de.raychouni.order.application.port.in;

import de.raychouni.order.domain.Order;
import lombok.Data;

import java.util.UUID;

@Data
public class UpdateOrderOfCompanyCommand {
    private final UUID companyId;
    private final UUID orderId;
    private final int scoreBoardNumber;
    private final Order.State state;
    private final String title;
}
