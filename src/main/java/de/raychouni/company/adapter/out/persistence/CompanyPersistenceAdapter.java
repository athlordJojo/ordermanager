package de.raychouni.company.adapter.out.persistence;

import de.raychouni.company.adapter.out.persistence.entities.CompanyJPA;
import de.raychouni.company.application.port.out.LoadAllCompaniesPort;
import de.raychouni.company.application.port.out.LoadCompanyByIdPort;
import de.raychouni.company.domain.Company;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class CompanyPersistenceAdapter implements LoadAllCompaniesPort, LoadCompanyByIdPort {
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

    @Override
    public Company loadCompanyById(UUID id) {
        CompanyJPA companyJPA = companyRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        return modelMapper.map(companyJPA, Company.class);
    }
}
