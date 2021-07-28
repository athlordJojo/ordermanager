package de.raychouni.ordernotifier.services;

import de.raychouni.ordernotifier.entities.Company;
import de.raychouni.order.adapter.out.persistence.CompanyRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;

    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    public List<Company> getAll() {
        return companyRepository.findAll();
    }
}
