package de.raychouni.order.application.port.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class CreateOrderForCompanyCommand {
    private UUID companyId;
    private int scoreBoardNumber;
}
