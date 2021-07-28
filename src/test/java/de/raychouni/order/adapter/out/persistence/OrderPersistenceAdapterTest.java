package de.raychouni.order.adapter.out.persistence;

import de.raychouni.order.adapter.out.persistence.entities.OrderJPA;
import de.raychouni.order.domain.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderPersistenceAdapterTest {

    @Mock
    OrderRepository orderRepository;

    OrderPersistenceAdapter adapter;

    OrderJPA order;
    private UUID uuid;

    @BeforeEach
    void setUp() {
        uuid = UUID.randomUUID();
        adapter = new OrderPersistenceAdapter(orderRepository, new ModelMapper());
        order = new OrderJPA();
        order.setState(OrderJPA.State.IN_PROGRESS);
        order.setUuid(uuid);
        order.setScoreBoardNumber(1);
        order.setTitle("title1");
    }

    @Test
    void loadOrdersOfCompany() {
        when(orderRepository.findAllByCompany_Uuid(uuid)).thenReturn(List.of(order));

        List<Order> orders = adapter.loadOrdersOfCompany(uuid);
        assertEquals(1, orders.size());
        Order domainOrder = orders.get(0);
        assertEquals(order.getTitle(), domainOrder.getTitle());
        assertEquals(order.getScoreBoardNumber(), domainOrder.getScoreBoardNumber());
        assertEquals(order.getUuid(), domainOrder.getUuid());
        assertEquals(order.getState().name(), domainOrder.getState().name());

    }
}
