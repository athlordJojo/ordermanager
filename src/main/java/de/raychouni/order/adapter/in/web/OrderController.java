package de.raychouni.order.adapter.in.web;

import de.raychouni.order.application.port.in.*;
import de.raychouni.order.domain.Order;
import de.raychouni.order.adapter.in.web.dtos.OrderDto;
import de.raychouni.order.adapter.out.persistence.entities.OrderJPA;
import de.raychouni.order.application.OrderService;
import de.raychouni.order.domain.OrderUpdate;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
@Slf4j
public class OrderController {

    private final ModelMapper modelMapper;
    private final GetAllOrdersForCompanyUseCase getAllOrdersForCompanyUseCase;
    private final CreateOrderForCompanyUsecase createOrderForCompanyUsecase;
    private final DeleteOrderOfCompanyUseCase deleteOrderOfCompanyUseCase;
    private final UpdateOrderOfCompanyUseCase updateOrderOfCompanyUseCase;

    public OrderController(ModelMapper modelMapper,
                           GetAllOrdersForCompanyUseCase getAllOrdersForCompanyUseCase,
                           CreateOrderForCompanyUsecase createOrderForCompanyUsecase,
                           DeleteOrderOfCompanyUseCase deleteOrderOfCompanyUseCase,
                           UpdateOrderOfCompanyUseCase updateOrderOfCompanyUseCase) {
        this.modelMapper = modelMapper;
        this.getAllOrdersForCompanyUseCase = getAllOrdersForCompanyUseCase;
        this.createOrderForCompanyUsecase = createOrderForCompanyUsecase;
        this.deleteOrderOfCompanyUseCase = deleteOrderOfCompanyUseCase;
        this.updateOrderOfCompanyUseCase = updateOrderOfCompanyUseCase;
    }

    @GetMapping("/companies/{companyId}/orders")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public List<OrderDto> getAllOrders(@PathVariable("companyId") UUID companyId) {
        List<Order> all = getAllOrdersForCompanyUseCase.getAllOrdersByCompanyId(new GetAllOrdersForCompanyCommand(companyId));
        return all.stream().map(order -> modelMapper.map(order, OrderDto.class)).collect(Collectors.toList());
    }

    @PostMapping("/companies/{companyId}/orders")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderDto createOrder(@RequestBody OrderDto orderDto, @PathVariable("companyId") UUID companyId) {
        Order createdOrder = createOrderForCompanyUsecase.createOrder(
                new CreateOrderForCompanyCommand(companyId, orderDto.getScoreBoardNumber()));
        return modelMapper.map(createdOrder, OrderDto.class);
    }

    @PutMapping("/companies/{companyId}/orders/{orderId}")
    @ResponseStatus(HttpStatus.OK)
    public OrderDto updateOrder(@RequestBody OrderDto orderDto, @PathVariable("companyId") UUID companyId,
                                @PathVariable("orderId") UUID orderId) {
        UpdateOrderOfCompanyCommand updateOrderOfCompanyCommand = new UpdateOrderOfCompanyCommand(companyId, orderId, orderDto.getScoreBoardNumber(), Order.State.valueOf(orderDto.getState()), orderDto.getTitle());
        Order updatedOrder = updateOrderOfCompanyUseCase.updateOrder(updateOrderOfCompanyCommand);
        return modelMapper.map(updatedOrder, OrderDto.class);
    }

    @DeleteMapping("/companies/{companyId}/orders/{orderId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteOrder(@PathVariable("companyId") UUID companyId,
                                            @PathVariable("orderId") UUID orderId) {
        deleteOrderOfCompanyUseCase.deleteOrderOfCompany(new DeleteOrderOfCompanyCommand(companyId, orderId));
        return ResponseEntity.noContent().build();
    }
}
