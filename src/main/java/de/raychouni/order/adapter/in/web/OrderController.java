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

    private final OrderService orderService;
    private final ModelMapper modelMapper;
    private SimpMessagingTemplate simpMessagingTemplate;
    private final GetAllOrdersForCompanyUseCase getAllOrdersForCompanyUseCase;
    private final CreateOrderForCompanyUsecase createOrderForCompanyUsecase;
    private final DeleteOrderOfCompanyUseCase deleteOrderOfCompanyUseCase;

    public OrderController(OrderService orderService, ModelMapper modelMapper,
                           SimpMessagingTemplate simpMessagingTemplate,
                           GetAllOrdersForCompanyUseCase getAllOrdersForCompanyUseCase,
                           CreateOrderForCompanyUsecase createOrderForCompanyUsecase,
                           DeleteOrderOfCompanyUseCase deleteOrderOfCompanyUseCase) {
        this.orderService = orderService;
        this.modelMapper = modelMapper;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.getAllOrdersForCompanyUseCase = getAllOrdersForCompanyUseCase;
        this.createOrderForCompanyUsecase = createOrderForCompanyUsecase;
        this.deleteOrderOfCompanyUseCase = deleteOrderOfCompanyUseCase;
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
        OrderJPA updatedOrder = orderService.updateOrder(modelMapper.map(orderDto, OrderJPA.class), companyId, orderId);
        return modelMapper.map(updatedOrder, OrderDto.class);
    }

    @DeleteMapping("/companies/{companyId}/orders/{orderId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteOrder(@PathVariable("companyId") UUID companyId,
                                            @PathVariable("orderId") UUID orderId) {
        deleteOrderOfCompanyUseCase.deleteOrderOfCompany(new DeleteOrderOfCompanyCommand(companyId, orderId));
        return ResponseEntity.noContent().build();
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onEntityUpdate(OrderUpdate orderUpdate) {
        log.debug("Sending order update");
        simpMessagingTemplate.convertAndSend("/topic/orders", orderUpdate);
    }

}
