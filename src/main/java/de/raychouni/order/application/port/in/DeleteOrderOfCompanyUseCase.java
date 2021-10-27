package de.raychouni.order.application.port.in;

import lombok.NonNull;

import java.util.UUID;

public interface DeleteOrderOfCompanyUseCase {
    void deleteOrderOfCompany(DeleteOrderOfCompanyCommand command);
}
