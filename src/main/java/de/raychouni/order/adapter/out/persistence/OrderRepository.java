package de.raychouni.order.adapter.out.persistence;

import de.raychouni.ordernotifier.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

    List<Order> findAllByCompany_Uuid(UUID uuid);

    Optional<Order> findFirstByUuidAndCompany_Uuid(UUID uuid, UUID companyUuid);
}
