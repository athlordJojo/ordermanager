package de.raychouni.ordernotifier.services;

import de.raychouni.company.adapter.out.persistence.CompanyRepository;
import de.raychouni.company.application.port.out.LoadCompanyByIdPort;
import de.raychouni.company.domain.Company;
import de.raychouni.order.adapter.in.web.dtos.OrderDto;
import de.raychouni.order.adapter.out.persistence.OrderRepository;
import de.raychouni.order.application.OrderService;
import de.raychouni.order.application.port.in.CreateOrderForCompanyCommand;
import de.raychouni.order.application.port.in.DeleteOrderOfCompanyCommand;
import de.raychouni.order.application.port.in.GetAllOrdersForCompanyCommand;
import de.raychouni.order.application.port.out.*;
import de.raychouni.order.domain.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.UUID;

import static de.raychouni.order.domain.OrderUpdate.CHANGE_TYPE.DELETED;
import static de.raychouni.order.domain.OrderUpdate.CHANGE_TYPE.INSERTED;
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

    @Mock
    DeleteOrderOfCompanyPort deleteOrderOfCompanyPort;

    @Mock
    CreateOrderPort createOrderPort;

    @Mock
    OrderChangedPort orderChangedPort;

    @Mock
    LoadCompanyByIdPort loadCompanyByIdPort;

    @Mock
    LoadOrderOfCompanyPort loadOrderOfCompanyPort;

    @Mock
    UpdateOrderOfCompanyPort updateOrderOfCompanyPort;

    private OrderService orderService;
    private Order order;
    private Company company;

    @BeforeEach
    void setUp() {
        company = new Company();
        company.setUuid(UUID.randomUUID());

        orderService = new OrderService(orderRepository, companyRepository, eventPublisher, createOrderPort, loadOrderOfCompanyPort, loadOrdersOfCompanyPort, deleteOrderOfCompanyPort, orderChangedPort, loadCompanyByIdPort, updateOrderOfCompanyPort);

        order = new Order();
        order.setUuid(UUID.randomUUID());
        order.setState(Order.State.IN_PROGRESS);
        order.setCompany(company);
        order.setScoreBoardNumber(1);
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

    @Test
    void createOrder_withNonExistingCompanyId_expectException() {
        doThrow(new EntityNotFoundException()).when(loadCompanyByIdPort).loadCompanyById(company.getUuid());
        assertThrows(EntityNotFoundException.class, () -> {
            orderService.createOrder(new CreateOrderForCompanyCommand(company.getUuid(), order.getScoreBoardNumber()));
        });

        verify(orderChangedPort, never()).sendOrderChangedMessage(any());
    }

    @Test
    void createOrder_withExistingCompanyId_expectSuccess() {
        Order orderWithoutId = new Order();
        orderWithoutId.setState(Order.State.IN_PROGRESS);
        orderWithoutId.setCompany(company);
        orderWithoutId.setScoreBoardNumber(1);

        assertNull(orderWithoutId.getUuid());
        when(loadCompanyByIdPort.loadCompanyById(company.getUuid())).thenReturn(company);
        Order orderWithId = new Order();
        orderWithId.setUuid(UUID.randomUUID());
        orderWithId.setState(Order.State.IN_PROGRESS);
        orderWithId.setCompany(company);
        orderWithId.setScoreBoardNumber(1);
        when(createOrderPort.createOrder(orderWithoutId)).thenReturn(orderWithId);

        Order savedNewOrder = orderService.createOrder(new CreateOrderForCompanyCommand(company.getUuid(), orderWithoutId.getScoreBoardNumber()));
        assertSame(orderWithId, savedNewOrder);
        verify(orderChangedPort).sendOrderChangedMessage(INSERTED);
    }
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
    @Test
    void deleteOrder_withNonExistingCompanyOrderCombination_expectException() {
        doThrow(new EntityNotFoundException()).when(deleteOrderOfCompanyPort).deleteOrderOfCompany(company.getUuid(), order.getUuid());
        assertThrows(EntityNotFoundException.class, () -> {
            orderService.deleteOrderOfCompany(new DeleteOrderOfCompanyCommand(company.getUuid(), order.getUuid()));
        });

        verify(deleteOrderOfCompanyPort).deleteOrderOfCompany(company.getUuid(), order.getUuid());
        verify(orderChangedPort, never()).sendOrderChangedMessage(DELETED);
    }

    @Test
    void deleteOrder_withExistingCompanyOrderCombination_expectSuccess() {
        when(deleteOrderOfCompanyPort.deleteOrderOfCompany(company.getUuid(), order.getUuid())).thenReturn(order);

        orderService.deleteOrderOfCompany(new DeleteOrderOfCompanyCommand(company.getUuid(), order.getUuid()));

        verify(deleteOrderOfCompanyPort).deleteOrderOfCompany(company.getUuid(), order.getUuid());
        verify(orderChangedPort).sendOrderChangedMessage(DELETED);
    }
}
