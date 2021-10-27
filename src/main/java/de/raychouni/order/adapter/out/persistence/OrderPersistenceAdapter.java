package de.raychouni.order.adapter.out.persistence;

import de.raychouni.company.adapter.out.persistence.CompanyRepository;
import de.raychouni.company.adapter.out.persistence.entities.CompanyJPA;
import de.raychouni.order.adapter.out.persistence.entities.OrderJPA;
import de.raychouni.order.application.port.out.*;
import de.raychouni.order.domain.Order;
import lombok.NonNull;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class OrderPersistenceAdapter implements LoadOrdersOfCompanyPort, LoadOrderOfCompanyPort,  DeleteOrderOfCompanyPort, CreateOrderPort, UpdateOrderOfCompanyPort {
    private final OrderRepository orderRepository;
    private final CompanyRepository companyRepository;
    private final ModelMapper modelMapper;

    public OrderPersistenceAdapter(OrderRepository orderRepository, CompanyRepository companyRepository,  ModelMapper modelMapper) {
        this.orderRepository = orderRepository;
        this.companyRepository = companyRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public Order loadOrderOfCompany(@NonNull UUID companyId, @NonNull UUID orderId) {
        OrderJPA orderJPA = getOrderJPAFromOrder(companyId, orderId);
        return modelMapper.map(orderJPA, Order.class);
    }

    @Override
    public List<Order> loadOrdersOfCompany(@NonNull UUID companyUuid) {
        List<OrderJPA> ordersOfCompany = orderRepository.findAllByCompany_Uuid(companyUuid);
        return ordersOfCompany.stream().map(order -> modelMapper.map(order, Order.class)).collect(Collectors.toList());
    }

    @Override
    public Order deleteOrderOfCompany(@NonNull UUID companyId, @NonNull UUID orderId) {
        OrderJPA orderJpa = getOrderJPAFromOrder(companyId, orderId);
        orderJpa.getCompany().deleteOrder(orderJpa);
        Order order = modelMapper.map(orderJpa, Order.class);
        orderRepository.delete(orderJpa);
        return order;
    }

    @Override
    public Order createOrder(@NonNull Order orderToCreate) {
        CompanyJPA c = companyRepository.findById(orderToCreate.getCompany().getUuid()).orElseThrow(EntityNotFoundException::new);
        OrderJPA orderJPA = modelMapper.map(orderToCreate, OrderJPA.class);
        orderRepository.saveAndFlush(orderJPA);
        c.addOrder(orderJPA);
        companyRepository.save(c);
        return modelMapper.map(orderJPA, Order.class);
    }

    @Override
    public Order updateOrderOfCompany(@NonNull Order orderToUpdate) {
        OrderJPA orderJPA = getOrderJPAFromOrder(orderToUpdate.getCompany().getUuid(), orderToUpdate.getUuid());
        // TODO: may also use mapper here ?
//        modelMapper.map(order, orderJPA); // does not work, fails with:
        orderJPA.setScoreBoardNumber(orderToUpdate.getScoreBoardNumber());
        orderJPA.setTitle(orderToUpdate.getTitle());
        orderJPA.setState(OrderJPA.State.valueOf(orderToUpdate.getState().name()));
        orderRepository.save(orderJPA);
        return modelMapper.map(orderJPA, Order.class);
    }

    private OrderJPA getOrderJPAFromOrder(@NonNull UUID companyId, @NonNull UUID orderId) {
        return orderRepository.findFirstByUuidAndCompany_Uuid(orderId, companyId)
                .orElseThrow(() -> new EntityNotFoundException("Could not find Order with id" + orderId + " for companyId: " + companyId));
    }
}
