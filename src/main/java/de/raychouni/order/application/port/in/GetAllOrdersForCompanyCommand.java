package de.raychouni.order.application.port.in;

import lombok.Getter;
import lombok.NonNull;

import java.util.UUID;

@Getter
public class GetAllOrdersForCompanyCommand {
    private UUID companyUuid;

    public GetAllOrdersForCompanyCommand(@NonNull UUID companyUuid) {
        this.companyUuid = companyUuid;
    }
}
