package de.raychouni.order.application;

import de.raychouni.company.adapter.out.persistence.entities.CompanyJPA;
import de.raychouni.order.adapter.out.persistence.entities.OrderJPA;
import de.raychouni.company.adapter.out.persistence.CompanyRepository;
import de.raychouni.order.adapter.out.persistence.OrderRepository;
import de.raychouni.order.application.port.in.GetAllOrdersForCompanyCommand;
import de.raychouni.order.application.port.in.GetAllOrdersForCompanyUseCase;
import de.raychouni.order.application.port.out.LoadOrdersOfCompanyPort;
import de.raychouni.order.domain.Order;
import de.raychouni.ordernotifier.services.OrderUpdate;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;

import static de.raychouni.ordernotifier.services.OrderUpdate.CHANGE_TYPE.*;

@Service
@Transactional
public class OrderService implements GetAllOrdersForCompanyUseCase {
    private final OrderRepository orderRepository;
    private final CompanyRepository companyRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final LoadOrdersOfCompanyPort loadOrdersOfCompanyPort;

    public OrderService(OrderRepository orderRepository, CompanyRepository companyRepository, ApplicationEventPublisher eventPublisher, LoadOrdersOfCompanyPort loadOrdersOfCompanyPort) {
        this.orderRepository = orderRepository;
        this.companyRepository = companyRepository;
        this.eventPublisher = eventPublisher;
        this.loadOrdersOfCompanyPort = loadOrdersOfCompanyPort;
    }

    @Override
    public List<Order> getAllOrdersByCompanyId(GetAllOrdersForCompanyCommand getAllOrdersForCompanyCommand) {
        return loadOrdersOfCompanyPort.loadOrdersOfCompany(getAllOrdersForCompanyCommand.getCompanyUuid());
    }

    public OrderJPA createOrder(UUID companyId, OrderJPA order) {
        CompanyJPA c = companyRepository.findById(companyId).orElseThrow(EntityNotFoundException::new);
        orderRepository.saveAndFlush(order);
        c.addOrder(order);
        companyRepository.save(c);
        publishChange(INSERTED);
        return order;
    }

    public OrderJPA updateOrder(OrderJPA updateOrder, UUID companyId, UUID orderToUpdate) {
        return orderRepository.findFirstByUuidAndCompany_Uuid(orderToUpdate, companyId).map(existingOrder -> {
            existingOrder.setState(updateOrder.getState());
            existingOrder.setScoreBoardNumber(updateOrder.getScoreBoardNumber());
            existingOrder.setTitle(updateOrder.getTitle());
            publishChange(UPDATED);
            return orderRepository.save(existingOrder);
        }).orElseThrow(() -> new EntityNotFoundException("Could not find Order with id" + orderToUpdate + " for companyId: " + companyId));
    }

    public void deleteOrder(UUID companyId, UUID orderId) {
        OrderJPA order = orderRepository.findFirstByUuidAndCompany_Uuid(orderId, companyId)
                .orElseThrow(() -> new EntityNotFoundException("Could not find Order with id" + orderId + " for companyId: " + companyId));
        orderRepository.delete(order);
        publishChange(DELETED);
    }

    private void publishChange(OrderUpdate.CHANGE_TYPE changeType) {
        eventPublisher.publishEvent(new OrderUpdate(changeType));
    }
}
