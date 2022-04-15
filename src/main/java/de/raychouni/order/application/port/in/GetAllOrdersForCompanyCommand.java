package de.raychouni.order.application.port.in;

import java.util.UUID;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Data
public class GetAllOrdersForCompanyCommand {
    private UUID companyUuid;

    public GetAllOrdersForCompanyCommand(@NonNull UUID companyUuid) {
        this.companyUuid = companyUuid;
    }
}
