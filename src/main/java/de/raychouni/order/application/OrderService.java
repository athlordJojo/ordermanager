package de.raychouni.order.application;

import de.raychouni.company.application.port.out.LoadCompanyByIdPort;
import de.raychouni.company.domain.Company;
import de.raychouni.order.application.port.in.*;
import de.raychouni.order.application.port.out.*;
import de.raychouni.order.domain.Order;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

import static de.raychouni.order.domain.OrderUpdate.CHANGE_TYPE.*;

@Service
@Transactional
public class OrderService implements GetAllOrdersForCompanyUseCase, CreateOrderForCompanyUsecase, UpdateOrderOfCompanyUseCase, DeleteOrderOfCompanyUseCase {
    private final CreateOrderPort createOrderPort;
    private final LoadOrderOfCompanyPort loadOrderOfCompanyPort;
    private final LoadOrdersOfCompanyPort loadOrdersOfCompanyPort;
    private final DeleteOrderOfCompanyPort deleteOrderOfCompanyPort;
    private final OrderChangedPort orderChangedPort;
    private final LoadCompanyByIdPort loadCompanyByIdPort;
    private final UpdateOrderOfCompanyPort updateOrderOfCompanyPort;


    public OrderService(CreateOrderPort createOrderPort, LoadOrderOfCompanyPort loadOrderOfCompanyPort, LoadOrdersOfCompanyPort loadOrdersOfCompanyPort, DeleteOrderOfCompanyPort deleteOrderOfCompanyPort, OrderChangedPort orderChangedPort, LoadCompanyByIdPort loadCompanyByIdPort, UpdateOrderOfCompanyPort updateOrderOfCompanyPort) {
        this.createOrderPort = createOrderPort;
        this.loadOrderOfCompanyPort = loadOrderOfCompanyPort;
        this.loadOrdersOfCompanyPort = loadOrdersOfCompanyPort;
        this.deleteOrderOfCompanyPort = deleteOrderOfCompanyPort;
        this.orderChangedPort = orderChangedPort;
        this.loadCompanyByIdPort = loadCompanyByIdPort;
        this.updateOrderOfCompanyPort = updateOrderOfCompanyPort;
    }

    @Override
    public List<Order> getAllOrdersByCompanyId(GetAllOrdersForCompanyCommand getAllOrdersForCompanyCommand) {
        return loadOrdersOfCompanyPort.loadOrdersOfCompany(getAllOrdersForCompanyCommand.getCompanyUuid());
    }

    @Override
    public void deleteOrderOfCompany(@NonNull DeleteOrderOfCompanyCommand deleteOrderOfCommand) {
        deleteOrderOfCompanyPort.deleteOrderOfCompany(deleteOrderOfCommand.getCompanyId(), deleteOrderOfCommand.getOrderID());
        orderChangedPort.sendOrderChangedMessage(DELETED);
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

    @Override
    public Order updateOrder(UpdateOrderOfCompanyCommand updateOrderOfCompanyCommand) {
        Order order = loadOrderOfCompanyPort.loadOrderOfCompany(updateOrderOfCompanyCommand.getCompanyId(), updateOrderOfCompanyCommand.getOrderId());
        order.setScoreBoardNumber(updateOrderOfCompanyCommand.getScoreBoardNumber());
        order.setTitle(updateOrderOfCompanyCommand.getTitle());
        order.setState(updateOrderOfCompanyCommand.getState());

        Order updatedOrder = updateOrderOfCompanyPort.updateOrderOfCompany(order);
        orderChangedPort.sendOrderChangedMessage(UPDATED);
        return updatedOrder;
    }
}
