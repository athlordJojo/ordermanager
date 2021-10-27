package de.raychouni.company.application;

import de.raychouni.company.application.port.in.GetAllCompaniesUseCase;
import de.raychouni.company.application.port.out.LoadAllCompaniesPort;
import de.raychouni.company.domain.Company;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompanyService implements GetAllCompaniesUseCase {

    private final LoadAllCompaniesPort loadAllCompaniesPort;

    public CompanyService(LoadAllCompaniesPort loadAllCompaniesPort) {
        this.loadAllCompaniesPort = loadAllCompaniesPort;
    }

    @Override
    public List<Company> getAllCompanies() {
        return loadAllCompaniesPort.loadAllCompanies();
    }
}
