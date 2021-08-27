package de.raychouni.order.application.port.in;

import lombok.Data;
import lombok.Getter;
import lombok.NonNull;

import java.util.UUID;

@Getter
@Data
public class GetAllOrdersForCompanyCommand {
    private UUID companyUuid;

    public GetAllOrdersForCompanyCommand(@NonNull UUID companyUuid) {
        this.companyUuid = companyUuid;
    }
}
