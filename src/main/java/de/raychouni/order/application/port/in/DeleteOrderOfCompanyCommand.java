package de.raychouni.order.application.port.in;

import java.util.UUID;
import lombok.Data;
import lombok.NonNull;

@Data
public class DeleteOrderOfCompanyCommand {
    private UUID companyId;
    private UUID orderID;

    public DeleteOrderOfCompanyCommand(@NonNull UUID companyId, @NonNull UUID orderID) {
        this.companyId = companyId;
        this.orderID = orderID;
    }
}
