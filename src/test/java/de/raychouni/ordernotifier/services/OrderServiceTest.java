package de.raychouni.ordernotifier.services;

import de.raychouni.ordernotifier.repos.CompanyRepository;
import de.raychouni.ordernotifier.repos.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    CompanyRepository companyRepository;

    @Mock
    OrderRepository orderRepository;

    @Mock
    ApplicationEventPublisher applicationPushPublisher;

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderService(orderRepository, companyRepository, applicationPushPublisher);
    }

    @Test
    void createOrder() {
    }
}