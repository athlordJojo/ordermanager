package de.raychouni.company.application.port.in;

import de.raychouni.company.domain.Company;

import java.util.List;

public interface GetAllCompaniesUseCase {
    List<Company> getAllCompanies();
}
