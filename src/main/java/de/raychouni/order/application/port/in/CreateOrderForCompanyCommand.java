package de.raychouni.order.application.port.in;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateOrderForCompanyCommand {
    private UUID companyId;
    private int scoreBoardNumber;
}
