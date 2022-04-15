package de.raychouni.order.adapter.out.persistence;

import de.raychouni.order.adapter.out.persistence.entities.OrderJPA;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<OrderJPA, UUID> {

    List<OrderJPA> findAllByCompany_Uuid(UUID uuid);

    Optional<OrderJPA> findFirstByUuidAndCompany_Uuid(UUID uuid, UUID companyUuid);
}
