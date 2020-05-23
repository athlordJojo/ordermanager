package de.raychouni.ordernotifier.repos;

import de.raychouni.ordernotifier.entities.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CompanyRepository extends JpaRepository<Company, UUID> {
}
