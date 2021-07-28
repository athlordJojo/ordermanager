package de.raychouni.company.adapter.out.persistence;

import de.raychouni.company.adapter.out.persistence.entities.CompanyJPA;
import de.raychouni.company.application.port.out.LoadAllCompaniesPort;
import de.raychouni.company.domain.Company;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CompanyPersistenceAdapter implements LoadAllCompaniesPort {
    private final CompanyRepository companyRepository;
    private final ModelMapper modelMapper;

    public CompanyPersistenceAdapter(CompanyRepository companyRepository, ModelMapper modelMapper) {
        this.companyRepository = companyRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<Company> loadAllCompanies() {
        List<CompanyJPA> all = companyRepository.findAll();
        return all.stream()
                .map(company -> modelMapper.map(company, Company.class))
                .collect(Collectors.toList());
    }
}
