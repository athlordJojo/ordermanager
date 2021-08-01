package de.raychouni.company.application.port.out;

import de.raychouni.company.domain.Company;

import java.util.UUID;

public interface LoadCompanyByIdPort {
    Company loadCompanyById(UUID id);
}
