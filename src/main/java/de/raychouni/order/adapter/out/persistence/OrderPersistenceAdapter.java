package de.raychouni.order.adapter.out.persistence;

import de.raychouni.company.adapter.out.persistence.CompanyRepository;
import de.raychouni.company.adapter.out.persistence.entities.CompanyJPA;
import de.raychouni.order.adapter.out.persistence.entities.OrderJPA;
import de.raychouni.order.application.port.out.CreateOrderPort;
import de.raychouni.order.application.port.out.DeleteOrderOfCompanyPort;
import de.raychouni.order.application.port.out.LoadOrdersOfCompanyPort;
import de.raychouni.order.domain.Order;
import lombok.NonNull;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class OrderPersistenceAdapter implements LoadOrdersOfCompanyPort, DeleteOrderOfCompanyPort, CreateOrderPort {
    private final OrderRepository orderRepository;
    private final CompanyRepository companyRepository;
    private final ModelMapper modelMapper;

    public OrderPersistenceAdapter(OrderRepository orderRepository, CompanyRepository companyRepository,  ModelMapper modelMapper) {
        this.orderRepository = orderRepository;
        this.companyRepository = companyRepository;
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
        orderJpa.getCompany().deleteOrder(orderJpa);
        Order order = modelMapper.map(orderJpa, Order.class);
        orderRepository.delete(orderJpa);
        return order;
    }

    @Override
    public Order createOrder(@NonNull Order orderToCreate) {
        // TODO: use other adapter here ?
        CompanyJPA c = companyRepository.findById(orderToCreate.getCompany().getUuid()).orElseThrow(EntityNotFoundException::new);
        OrderJPA orderJPA = modelMapper.map(orderToCreate, OrderJPA.class);
        orderRepository.saveAndFlush(orderJPA);
        c.addOrder(orderJPA);
        companyRepository.save(c);
        return modelMapper.map(orderJPA, Order.class);
    }
}
