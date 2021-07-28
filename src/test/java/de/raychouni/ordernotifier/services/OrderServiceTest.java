package de.raychouni.ordernotifier.services;

import de.raychouni.order.adapter.out.persistence.entities.CompanyJPA;
import de.raychouni.order.adapter.out.persistence.entities.OrderJPA;
import de.raychouni.order.adapter.out.persistence.CompanyRepository;
import de.raychouni.order.adapter.out.persistence.OrderRepository;
import de.raychouni.order.application.OrderService;
import de.raychouni.order.application.port.in.GetAllOrdersForCompanyCommand;
import de.raychouni.order.application.port.out.LoadOrdersOfCompanyPort;
import de.raychouni.order.domain.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static de.raychouni.order.adapter.out.persistence.entities.OrderJPA.State.READY;
import static de.raychouni.ordernotifier.services.OrderUpdate.CHANGE_TYPE.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    CompanyRepository companyRepository;

    @Mock
    OrderRepository orderRepository;

    @Mock
    ApplicationEventPublisher eventPublisher;

    @Mock
    LoadOrdersOfCompanyPort loadOrdersOfCompanyPort;

    private OrderService orderService;
    private Order order;
    private CompanyJPA company;

    @BeforeEach
    void setUp() {
        orderService = new OrderService(orderRepository, companyRepository, eventPublisher, loadOrdersOfCompanyPort);
        order = new Order();
        order.setState(Order.State.IN_PROGRESS);
        order.setUuid(UUID.randomUUID());
        order.setScoreBoardNumber(1);
        order.setTitle("title1");
        company = new CompanyJPA();
        company.setUuid(UUID.randomUUID());
    }

    @Test
    void getAllOrdersByCompanyId() {
        when(loadOrdersOfCompanyPort.loadOrdersOfCompany(company.getUuid())).thenReturn(List.of(order));
        List<Order> ordersByCompanyId = orderService.getAllOrdersByCompanyId(new GetAllOrdersForCompanyCommand(company.getUuid()));
        assertNotNull(ordersByCompanyId);
        assertEquals(1, ordersByCompanyId.size());
        assertEquals(order, ordersByCompanyId.get(0));
        verify(eventPublisher, never()).publishEvent(any());
    }
//
//    @Test
//    void createOrder_withNonExistingCompanyId_expectException() {
//        when(companyRepository.findById(company.getUuid())).thenReturn(Optional.empty());
//        assertThrows(EntityNotFoundException.class, () -> {
//            orderService.createOrder(company.getUuid(), order);
//        });
//
//        verify(eventPublisher, never()).publishEvent(any());
//    }
//
//    @Test
//    void createOrder_withExistingCompanyId_expectSuccess() {
//        Optional<CompanyJPA> optionalCompany = Optional.of(company);
//        when(companyRepository.findById(company.getUuid())).thenReturn(optionalCompany);
//        orderService.createOrder(company.getUuid(), order);
//
//        assertEquals(1, company.getOrders().size());
//
//        verify(orderRepository).saveAndFlush(order);
//        verify(companyRepository).save(company);
//        verify(eventPublisher).publishEvent(eq(new OrderUpdate(INSERTED)));
//    }
//
//    @Test
//    void updateOrder_withNonExistingCompanyOrderCombination_expectException() {
//        when(orderRepository.findFirstByUuidAndCompany_Uuid(order.getUuid(), company.getUuid())).thenReturn(Optional.empty());
//        assertThrows(EntityNotFoundException.class, () -> {
//            orderService.updateOrder(new OrderJPA(), company.getUuid(), order.getUuid());
//        });
//        verify(orderRepository).findFirstByUuidAndCompany_Uuid(order.getUuid(), company.getUuid());
//        verify(eventPublisher, never()).publishEvent(any());
//        verify(orderRepository, never()).save(any());
//    }
//
//
//    @Test
//    void updateOrder_withExistingCompanyOrderCombination_expectSuccess() {
//        // precondition check
//        assertEquals(OrderJPA.State.IN_PROGRESS, order.getState());
//        assertEquals(1, order.getScoreBoardNumber());
//        assertEquals("title1", order.getTitle());
//
//        when(orderRepository.findFirstByUuidAndCompany_Uuid(order.getUuid(), company.getUuid())).thenReturn(Optional.of(order));
//        when(orderRepository.save(order)).thenReturn(order);
//
//        OrderJPA changeSetOrder = new OrderJPA();
//        changeSetOrder.setState(READY);
//        changeSetOrder.setScoreBoardNumber(2);
//        changeSetOrder.setTitle("title2");
//
//        OrderJPA updatedOrder = orderService.updateOrder(changeSetOrder, company.getUuid(), this.order.getUuid());
//
//        assertNotNull(updatedOrder);
//        assertEquals(order.getUuid(), updatedOrder.getUuid());
//        assertEquals(READY, updatedOrder.getState());
//        assertEquals(2, updatedOrder.getScoreBoardNumber());
//        assertEquals("title2", updatedOrder.getTitle());
//
//        verify(orderRepository).findFirstByUuidAndCompany_Uuid(this.order.getUuid(), company.getUuid());
//        verify(eventPublisher).publishEvent(eq(new OrderUpdate(UPDATED)));
//        verify(orderRepository).save(order);
//    }
//
//    @Test
//    void deleteOrder_withNonExistingCompanyOrderCombination_expectException() {
//        when(orderRepository.findFirstByUuidAndCompany_Uuid(order.getUuid(), company.getUuid())).thenReturn(Optional.empty());
//        assertThrows(EntityNotFoundException.class, () -> {
//            orderService.deleteOrder(company.getUuid(), order.getUuid());
//        });
//
//        verify(orderRepository).findFirstByUuidAndCompany_Uuid(order.getUuid(), company.getUuid());
//        verify(orderRepository, never()).delete(any());
//        verify(eventPublisher, never()).publishEvent(any());
//    }
//
//    @Test
//    void deleteOrder_withExistingCompanyOrderCombination_expectSuccess() {
//        when(orderRepository.findFirstByUuidAndCompany_Uuid(order.getUuid(), company.getUuid())).thenReturn(Optional.of(order));
//
//        orderService.deleteOrder(company.getUuid(), order.getUuid());
//
//        verify(orderRepository).findFirstByUuidAndCompany_Uuid(order.getUuid(), company.getUuid());
//        verify(orderRepository).delete(order);
//        verify(eventPublisher).publishEvent(new OrderUpdate(DELETED));
//    }
}
