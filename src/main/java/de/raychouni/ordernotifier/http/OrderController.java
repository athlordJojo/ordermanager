package de.raychouni.ordernotifier.http;

import de.raychouni.ordernotifier.dtos.OrderDto;
import de.raychouni.ordernotifier.entities.Order;
import de.raychouni.ordernotifier.services.OrderService;
import de.raychouni.ordernotifier.services.OrderUpdate;
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

    public OrderController(OrderService orderService, ModelMapper modelMapper, SimpMessagingTemplate simpMessagingTemplate) {
        this.orderService = orderService;
        this.modelMapper = modelMapper;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @GetMapping("/companies/{companyId}/orders")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public List<OrderDto> getAllOrders(@PathVariable("companyId") UUID companyId) {
        List<Order> all = orderService.getAllOrdersByCompanyId(companyId);
        return all.stream().map(order -> modelMapper.map(order, OrderDto.class)).collect(Collectors.toList());
    }

    @PostMapping("/companies/{companyId}/orders")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderDto createOrder(@RequestBody OrderDto orderDto, @PathVariable("companyId") UUID companyId) {
        Order createdOrder = orderService.createOrder(companyId, modelMapper.map(orderDto, Order.class));
        return modelMapper.map(createdOrder, OrderDto.class);
    }

    @PutMapping("/companies/{companyId}/orders/{orderId}")
    @ResponseStatus(HttpStatus.OK)
    public OrderDto updateOrder(@RequestBody OrderDto orderDto, @PathVariable("companyId") UUID companyId,
                                @PathVariable("orderId") UUID orderId) {
        Order updatedOrder = orderService.updateOrder(modelMapper.map(orderDto, Order.class), companyId, orderId);
        return modelMapper.map(updatedOrder, OrderDto.class);
    }

    @DeleteMapping("/companies/{companyId}/orders/{orderId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteOrder(@PathVariable("companyId") UUID companyId,
                                            @PathVariable("orderId") UUID orderId) {
        orderService.deleteOrder(companyId, orderId);
        return ResponseEntity.noContent().build();
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onEntityUpdate(OrderUpdate orderUpdate) {
        log.debug("Sending order update");
        simpMessagingTemplate.convertAndSend("/topic/orders", orderUpdate);
    }

}
