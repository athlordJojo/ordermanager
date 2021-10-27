package de.raychouni.order.adapter.out.persistence;

import de.raychouni.order.adapter.out.persistence.entities.OrderJPA;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<OrderJPA, UUID> {

    List<OrderJPA> findAllByCompany_Uuid(UUID uuid);

    Optional<OrderJPA> findFirstByUuidAndCompany_Uuid(UUID uuid, UUID companyUuid);
}
