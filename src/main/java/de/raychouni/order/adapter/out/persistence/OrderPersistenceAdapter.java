package de.raychouni.order.adapter.out.persistence;

import de.raychouni.order.adapter.out.persistence.entities.OrderJPA;
import de.raychouni.order.application.port.out.DeleteOrderOfCompanyPort;
import de.raychouni.order.application.port.out.LoadOrdersOfCompanyPort;
import de.raychouni.order.domain.Order;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class OrderPersistenceAdapter implements LoadOrdersOfCompanyPort, DeleteOrderOfCompanyPort {
    private final OrderRepository orderRepository;
    private final ModelMapper modelMapper;

    public OrderPersistenceAdapter(OrderRepository orderRepository, ModelMapper modelMapper) {
        this.orderRepository = orderRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<Order> loadOrdersOfCompany(UUID companyUuid) {
        List<OrderJPA> ordersOfCompany = orderRepository.findAllByCompany_Uuid(companyUuid);
        return ordersOfCompany.stream().map(order -> modelMapper.map(order, Order.class)).collect(Collectors.toList());
    }

    @Override
    public Order deleteOrderOfCompany(UUID companyId, UUID orderId) {
        OrderJPA orderJpa = orderRepository.findFirstByUuidAndCompany_Uuid(orderId, companyId)
                .orElseThrow(() -> new EntityNotFoundException("Could not find Order with id" + orderId + " for companyId: " + companyId));
        Order order = modelMapper.map(orderJpa, Order.class);
        orderRepository.delete(orderJpa);
        return order;
    }
}
