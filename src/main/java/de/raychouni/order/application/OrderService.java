package de.raychouni.order.application;

import de.raychouni.company.adapter.out.persistence.CompanyRepository;
import de.raychouni.company.application.port.out.LoadCompanyByIdPort;
import de.raychouni.company.domain.Company;
import de.raychouni.order.adapter.out.persistence.OrderRepository;
import de.raychouni.order.adapter.out.persistence.entities.OrderJPA;
import de.raychouni.order.application.port.in.*;
import de.raychouni.order.application.port.out.CreateOrderPort;
import de.raychouni.order.application.port.out.DeleteOrderOfCompanyPort;
import de.raychouni.order.application.port.out.LoadOrdersOfCompanyPort;
import de.raychouni.order.application.port.out.OrderChangedPort;
import de.raychouni.order.domain.Order;
import de.raychouni.order.domain.OrderUpdate;
import lombok.NonNull;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;

import static de.raychouni.order.domain.OrderUpdate.CHANGE_TYPE.*;

@Service
@Transactional
public class OrderService implements GetAllOrdersForCompanyUseCase, CreateOrderForCompanyUsecase, DeleteOrderOfCompanyUseCase {
    private final OrderRepository orderRepository;
    private final CompanyRepository companyRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final CreateOrderPort createOrderPort;
    private final LoadOrdersOfCompanyPort loadOrdersOfCompanyPort;
    private final DeleteOrderOfCompanyPort deleteOrderOfCompanyPort;
    private final OrderChangedPort orderChangedPort;
    private final LoadCompanyByIdPort loadCompanyByIdPort;


    public OrderService(OrderRepository orderRepository, CompanyRepository companyRepository, ApplicationEventPublisher eventPublisher, CreateOrderPort createOrderPort, LoadOrdersOfCompanyPort loadOrdersOfCompanyPort, DeleteOrderOfCompanyPort deleteOrderOfCompanyPort, OrderChangedPort orderChangedPort, LoadCompanyByIdPort loadCompanyByIdPort) {
        this.orderRepository = orderRepository;
        this.companyRepository = companyRepository;
        this.eventPublisher = eventPublisher;
        this.createOrderPort = createOrderPort;
        this.loadOrdersOfCompanyPort = loadOrdersOfCompanyPort;
        this.deleteOrderOfCompanyPort = deleteOrderOfCompanyPort;
        this.orderChangedPort = orderChangedPort;
        this.loadCompanyByIdPort = loadCompanyByIdPort;
    }

    @Override
    public List<Order> getAllOrdersByCompanyId(GetAllOrdersForCompanyCommand getAllOrdersForCompanyCommand) {
        return loadOrdersOfCompanyPort.loadOrdersOfCompany(getAllOrdersForCompanyCommand.getCompanyUuid());
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

    @Override
    public void deleteOrderOfCompany(@NonNull DeleteOrderOfCompanyCommand deleteOrderOfCommand) {
        deleteOrderOfCompanyPort.deleteOrderOfCompany(deleteOrderOfCommand.getCompanyId(), deleteOrderOfCommand.getOrderID());
        orderChangedPort.sendOrderChangedMessage(DELETED);
    }

    private void publishChange(OrderUpdate.CHANGE_TYPE changeType) {
        eventPublisher.publishEvent(new OrderUpdate(changeType));
    }

    @Override
    public Order createOrder(CreateOrderForCompanyCommand createOrderForCompanyCommand) {
        Company company = loadCompanyByIdPort.loadCompanyById(createOrderForCompanyCommand.getCompanyId());
        Order newOrder = new Order();
        newOrder.setState(Order.State.IN_PROGRESS);
        newOrder.setCompany(company);
        newOrder.setScoreBoardNumber(createOrderForCompanyCommand.getScoreBoardNumber());

        Order savedNewOrder = createOrderPort.createOrder(newOrder);
        orderChangedPort.sendOrderChangedMessage(INSERTED);
        return savedNewOrder;
    }
}
