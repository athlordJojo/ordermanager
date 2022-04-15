package de.raychouni.order.application.port.in;

import de.raychouni.order.domain.Order;
import java.util.UUID;
import lombok.Data;

@Data
public class UpdateOrderOfCompanyCommand {
    private final UUID companyId;
    private final UUID orderId;
    private final int scoreBoardNumber;
    private final Order.State state;
    private final String title;
}
