package de.raychouni.ordernotifier.services;

import de.raychouni.order.adapter.out.persistence.entities.CompanyJPA;
import de.raychouni.order.adapter.out.persistence.CompanyRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;

    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    public List<CompanyJPA> getAll() {
        return companyRepository.findAll();
    }
}
