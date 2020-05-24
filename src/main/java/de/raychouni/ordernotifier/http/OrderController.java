package de.raychouni.ordernotifier.http;

import de.raychouni.ordernotifier.dtos.OrderDto;
import de.raychouni.ordernotifier.entities.Order;
import de.raychouni.ordernotifier.services.OrderService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
public class OrderController {

    private final OrderService orderService;
    private final ModelMapper modelMapper;

    public OrderController(OrderService orderService, ModelMapper modelMapper) {
        this.orderService = orderService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/companies/{companyId}/orders")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public List<OrderDto> getAllOrders(@PathVariable("companyId") UUID companyId) {
        List<Order> all = orderService.getAllOrdersByCompanyId(companyId);
        List<OrderDto> orderDtos = all.stream().map(order -> modelMapper.map(order, OrderDto.class)).collect(Collectors.toList());
        return orderDtos;
    }

    @PostMapping("/companies/{companyId}/orders")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderDto createOrder(@RequestBody OrderDto orderDto, @PathVariable("companyId") UUID companyId) {
        Order createdOrder = orderService.createOrder(companyId, modelMapper.map(orderDto, Order.class));
        return modelMapper.map(createdOrder, OrderDto.class);
    }

//    @PutMapping("/companies/{companyId}/orders/{orderId}")
//    @ResponseStatus(HttpStatus.OK)
//    public OrderDto updateOrder(@RequestBody OrderDto orderDto, @PathVariable("companyId") UUID companyId,
//                                @PathVariable("orderId") UUID orderId){
//
//    }

}
