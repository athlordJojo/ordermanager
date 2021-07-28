package de.raychouni.company.adapter.out.persistence;

import de.raychouni.company.adapter.out.persistence.entities.CompanyJPA;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CompanyRepository extends JpaRepository<CompanyJPA, UUID> {
}
