package de.raychouni.order.adapter.out.persistence;

import de.raychouni.company.adapter.out.persistence.CompanyRepository;
import de.raychouni.company.adapter.out.persistence.entities.CompanyJPA;
import de.raychouni.company.domain.Company;
import de.raychouni.order.adapter.out.persistence.entities.OrderJPA;
import de.raychouni.order.domain.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderPersistenceAdapterTest {

    @Mock
    OrderRepository orderRepository;

    @Mock
    CompanyRepository companyRepository;

    OrderPersistenceAdapter adapter;

    OrderJPA orderJpa;
    private UUID orderId;
    private UUID companyId;
    private CompanyJPA companyJpa;

    @BeforeEach
    void setUp() {
        orderId = UUID.randomUUID();

        adapter = new OrderPersistenceAdapter(orderRepository, companyRepository, new ModelMapper());
        orderJpa = new OrderJPA();
        orderJpa.setState(OrderJPA.State.IN_PROGRESS);
        orderJpa.setUuid(orderId);
        orderJpa.setScoreBoardNumber(1);
        orderJpa.setTitle("title1");

        companyJpa = new CompanyJPA();
        companyId = UUID.randomUUID();
        companyJpa.setUuid(companyId);

        orderJpa.setCompany(companyJpa);
    }

    @Test
    void loadOrdersOfCompany() {
        when(orderRepository.findAllByCompany_Uuid(orderId)).thenReturn(List.of(orderJpa));

        List<Order> orders = adapter.loadOrdersOfCompany(orderId);
        assertEquals(1, orders.size());
        Order domainOrder = orders.get(0);
        assertEquals(orderJpa.getTitle(), domainOrder.getTitle());
        assertEquals(orderJpa.getScoreBoardNumber(), domainOrder.getScoreBoardNumber());
        assertEquals(orderJpa.getUuid(), domainOrder.getUuid());
        assertEquals(orderJpa.getState().name(), domainOrder.getState().name());

    }

    @Test
    void deleteOrderOfCompany_withNonExistingEntity_expectException() {
        when(orderRepository.findFirstByUuidAndCompany_Uuid(orderJpa.getUuid(), companyId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            adapter.deleteOrderOfCompany(companyId, orderId);
        });
    }

    @Test
    void deleteOrderOfCompany_withExistingEntity_expectSuccess() {
        when(orderRepository.findFirstByUuidAndCompany_Uuid(orderJpa.getUuid(), companyId)).thenReturn(Optional.of(orderJpa));

        adapter.deleteOrderOfCompany(companyId, orderId);
        verify(orderRepository).findFirstByUuidAndCompany_Uuid(orderId, companyId);
    }


    @Test
    void createOrderOfCompany_withExistingCompany(){
        when(companyRepository.findById(companyId)).thenReturn(Optional.ofNullable(companyJpa));
        Company company = new Company();
        company.setUuid(companyId);
        Order newOrder = new Order();
        newOrder.setCompany(company);

        Order order = adapter.createOrder(newOrder);
        assertNotNull(order);
        //making sure an id is generated
        verify(orderRepository).saveAndFlush(any(OrderJPA.class));
    }

    @Test
    void updateOrderOfCompany_withExistingCompany(){
        when(orderRepository.findFirstByUuidAndCompany_Uuid(orderId, companyId)).thenReturn(Optional.ofNullable(orderJpa));

        Company company = new Company();
        company.setUuid(companyId);
        Order updatedOrder = new Order();
        updatedOrder.setUuid(orderId);
        updatedOrder.setCompany(company);
        updatedOrder.setScoreBoardNumber(999);
        updatedOrder.setTitle("new title");
        updatedOrder.setState(Order.State.READY);

        Order resultOrder = adapter.updateOrderOfCompany(updatedOrder);

        assertEquals(999, resultOrder.getScoreBoardNumber());
        assertEquals("new title", resultOrder.getTitle());
        assertEquals(Order.State.READY, resultOrder.getState());
        assertNotNull(resultOrder);
        verify(orderRepository).save(orderJpa);
    }
}
