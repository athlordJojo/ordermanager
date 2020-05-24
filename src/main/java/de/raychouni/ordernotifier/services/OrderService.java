package de.raychouni.ordernotifier.services;

import de.raychouni.ordernotifier.entities.Company;
import de.raychouni.ordernotifier.entities.Order;
import de.raychouni.ordernotifier.repos.CompanyRepository;
import de.raychouni.ordernotifier.repos.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CompanyRepository companyRepository;

    public OrderService(OrderRepository orderRepository, CompanyRepository companyRepository) {
        this.orderRepository = orderRepository;
        this.companyRepository = companyRepository;
    }

    public List<Order> getAllOrdersByCompanyId(UUID customerId) {
        return orderRepository.findAllByCompany_Uuid(customerId);
    }

    public Order createOrder(UUID companyId, Order order) {
        Company c = companyRepository.findById(companyId).orElseThrow(EntityNotFoundException::new);
        orderRepository.saveAndFlush(order);
        c.addOrder(order);
        return order;
    }

    public Order updateOrder(Order order, UUID companyId, UUID orderId) {
        return orderRepository.findFirstByUuidAndCompany_Uuid(orderId, companyId).map(savedOrder -> {
            savedOrder.setState(order.getState());
            savedOrder.setScoreBoardNumber(order.getScoreBoardNumber());
            savedOrder.setTitle(order.getTitle());
            return orderRepository.save(savedOrder);
        }).orElseThrow(() -> new EntityNotFoundException("Could not find Order with id" + orderId + " for companyId: " + companyId));

    }
}
