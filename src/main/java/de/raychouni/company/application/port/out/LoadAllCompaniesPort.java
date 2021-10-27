package de.raychouni.company.application.port.out;

import de.raychouni.company.domain.Company;

import java.util.List;

public interface LoadAllCompaniesPort {
    List<Company> loadAllCompanies();
}
