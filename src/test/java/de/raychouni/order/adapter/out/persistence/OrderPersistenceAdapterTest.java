package de.raychouni.order.adapter.out.persistence;

import de.raychouni.company.adapter.out.persistence.entities.CompanyJPA;
import de.raychouni.order.adapter.out.persistence.entities.OrderJPA;
import de.raychouni.order.domain.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.provider.Arguments;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderPersistenceAdapterTest {

    @Mock
    OrderRepository orderRepository;

    OrderPersistenceAdapter adapter;

    OrderJPA order;
    private UUID orderId;
    private UUID companyId;

    @BeforeEach
    void setUp() {
        orderId = UUID.randomUUID();

        adapter = new OrderPersistenceAdapter(orderRepository, new ModelMapper());
        order = new OrderJPA();
        order.setState(OrderJPA.State.IN_PROGRESS);
        order.setUuid(orderId);
        order.setScoreBoardNumber(1);
        order.setTitle("title1");

        CompanyJPA company = new CompanyJPA();
        companyId = UUID.randomUUID();
        company.setUuid(companyId);

        order.setCompany(company);
    }

    @Test
    void loadOrdersOfCompany() {
        when(orderRepository.findAllByCompany_Uuid(orderId)).thenReturn(List.of(order));

        List<Order> orders = adapter.loadOrdersOfCompany(orderId);
        assertEquals(1, orders.size());
        Order domainOrder = orders.get(0);
        assertEquals(order.getTitle(), domainOrder.getTitle());
        assertEquals(order.getScoreBoardNumber(), domainOrder.getScoreBoardNumber());
        assertEquals(order.getUuid(), domainOrder.getUuid());
        assertEquals(order.getState().name(), domainOrder.getState().name());

    }

    @Test
    void deleteOrderOfCompany_withNonExistingEntity_expectException() {
        when(orderRepository.findFirstByUuidAndCompany_Uuid(order.getUuid(), companyId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            adapter.deleteOrderOfCompany(companyId, orderId);
        });
    }

    @Test
    void deleteOrderOfCompany_withExistingEntity_expectSuccess() {
        when(orderRepository.findFirstByUuidAndCompany_Uuid(order.getUuid(), companyId)).thenReturn(Optional.of(order));

        adapter.deleteOrderOfCompany(companyId, orderId);
        verify(orderRepository).findFirstByUuidAndCompany_Uuid(orderId, companyId);
    }

}
