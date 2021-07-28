package de.raychouni.ordernotifier.services;

import de.raychouni.order.adapter.out.persistence.entities.Company;
import de.raychouni.order.adapter.out.persistence.entities.Order;
import de.raychouni.order.adapter.out.persistence.CompanyRepository;
import de.raychouni.order.adapter.out.persistence.OrderRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;

import static de.raychouni.ordernotifier.services.OrderUpdate.CHANGE_TYPE.*;

@Service
@Transactional
public class OrderService {
    private final OrderRepository orderRepository;
    private final CompanyRepository companyRepository;
    private final ApplicationEventPublisher eventPublisher;

    public OrderService(OrderRepository orderRepository, CompanyRepository companyRepository, ApplicationEventPublisher eventPublisher) {
        this.orderRepository = orderRepository;
        this.companyRepository = companyRepository;
        this.eventPublisher = eventPublisher;
    }

    public List<Order> getAllOrdersByCompanyId(UUID companyId) {
        return orderRepository.findAllByCompany_Uuid(companyId);
    }

    public Order createOrder(UUID companyId, Order order) {
        Company c = companyRepository.findById(companyId).orElseThrow(EntityNotFoundException::new);
        orderRepository.saveAndFlush(order);
        c.addOrder(order);
        companyRepository.save(c);
        publishChange(INSERTED);
        return order;
    }

    public Order updateOrder(Order updateOrder, UUID companyId, UUID orderToUpdate) {
        return orderRepository.findFirstByUuidAndCompany_Uuid(orderToUpdate, companyId).map(existingOrder -> {
            existingOrder.setState(updateOrder.getState());
            existingOrder.setScoreBoardNumber(updateOrder.getScoreBoardNumber());
            existingOrder.setTitle(updateOrder.getTitle());
            publishChange(UPDATED);
            return orderRepository.save(existingOrder);
        }).orElseThrow(() -> new EntityNotFoundException("Could not find Order with id" + orderToUpdate + " for companyId: " + companyId));
    }

    public void deleteOrder(UUID companyId, UUID orderId) {
        Order order = orderRepository.findFirstByUuidAndCompany_Uuid(orderId, companyId)
                .orElseThrow(() -> new EntityNotFoundException("Could not find Order with id" + orderId + " for companyId: " + companyId));
        orderRepository.delete(order);
        publishChange(DELETED);
    }

    private void publishChange(OrderUpdate.CHANGE_TYPE changeType) {
        eventPublisher.publishEvent(new OrderUpdate(changeType));
    }
}
