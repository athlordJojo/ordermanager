package de.raychouni.order.adapter.in.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.raychouni.configuration.BaseConfiguration;
import de.raychouni.order.adapter.in.web.dtos.OrderDto;
import de.raychouni.order.application.port.in.*;
import de.raychouni.order.domain.Order;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@WebMvcTest(value = OrderController.class)
@Import(BaseConfiguration.class)
class OrderControllerTest {

    @Autowired MockMvc mockMvc;

    private final UUID companyId = UUID.randomUUID();
    private final String orderUrl = "/companies/" + companyId.toString() + "/orders";

    @MockBean GetAllOrdersForCompanyUseCase getAllOrdersForCompanyUseCase;

    @MockBean CreateOrderForCompanyUsecase createOrderForCompanyUsecase;

    @MockBean DeleteOrderOfCompanyUseCase deleteOrderOfCompanyUseCase;

    @MockBean UpdateOrderOfCompanyUseCase updateOrderOfCompanyUseCase;

    Order order1;
    Order order2;

    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        order1 = new Order();
        order1.setUuid(UUID.randomUUID());
        order1.setTitle("Order 1");

        order2 = new Order();
        order2.setUuid(UUID.randomUUID());
        order2.setTitle("Order 2");
    }

    @Test
    void getAllOrders() throws Exception {
        when(getAllOrdersForCompanyUseCase.getAllOrdersByCompanyId(
                        any(GetAllOrdersForCompanyCommand.class)))
                .thenReturn(List.of(order1, order2));

        String expectedResult =
                objectMapper.writeValueAsString(
                        List.of(getOrderDtoFromOrder(order1), getOrderDtoFromOrder(order2)));

        MockHttpServletRequestBuilder requestBuilder = get(orderUrl).accept(APPLICATION_JSON);
        mockMvc
                .perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResult))
                .andReturn();

        verify(getAllOrdersForCompanyUseCase)
                .getAllOrdersByCompanyId(eq(new GetAllOrdersForCompanyCommand(companyId)));
        verify(createOrderForCompanyUsecase, never()).createOrder(any());
        verify(updateOrderOfCompanyUseCase, never()).updateOrder(any());
        verify(deleteOrderOfCompanyUseCase, never()).deleteOrderOfCompany(any());
    }

    @Test
    void createOrder() throws Exception {
        when(createOrderForCompanyUsecase.createOrder(any())).thenReturn(order1);

        int scoreBoardNumber = 1;
        OrderDto orderDto = new OrderDto();
        orderDto.setScoreBoardNumber(scoreBoardNumber);
        MockHttpServletRequestBuilder requestBuilder =
                post(orderUrl)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderDto))
                        .accept(APPLICATION_JSON);

        String expectedResult = objectMapper.writeValueAsString(getOrderDtoFromOrder(order1));
        mockMvc
                .perform(requestBuilder)
                .andExpect(status().isCreated())
                .andExpect(content().json(expectedResult))
                .andReturn();

        verify(getAllOrdersForCompanyUseCase, never()).getAllOrdersByCompanyId(any());
        verify(createOrderForCompanyUsecase)
                .createOrder(eq(new CreateOrderForCompanyCommand(companyId, scoreBoardNumber)));
        verify(updateOrderOfCompanyUseCase, never()).updateOrder(any());
        verify(deleteOrderOfCompanyUseCase, never()).deleteOrderOfCompany(any());
    }

    @Test
    void updateOrder() throws Exception {
        when(updateOrderOfCompanyUseCase.updateOrder(any())).thenReturn(order1);

        int scoreBoardNumber = 1;
        OrderDto orderDto = new OrderDto();
        orderDto.setState("IN_PROGRESS");
        orderDto.setTitle("New Title");
        orderDto.setScoreBoardNumber(scoreBoardNumber);
        MockHttpServletRequestBuilder requestBuilder =
                put(orderUrl + "/" + order1.getUuid().toString())
                        .content(objectMapper.writeValueAsString(orderDto))
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON);

        String expectedResult = objectMapper.writeValueAsString(getOrderDtoFromOrder(order1));
        mockMvc
                .perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResult))
                .andReturn();

        verify(getAllOrdersForCompanyUseCase, never()).getAllOrdersByCompanyId(any());
        verify(createOrderForCompanyUsecase, never()).createOrder(any());
        verify(updateOrderOfCompanyUseCase)
                .updateOrder(
                        eq(
                                new UpdateOrderOfCompanyCommand(
                                        companyId, order1.getUuid(), 1, Order.State.IN_PROGRESS, "New Title")));
        verify(deleteOrderOfCompanyUseCase, never()).deleteOrderOfCompany(any());
    }

    @Test
    void deleteOrder() throws Exception {
        MockHttpServletRequestBuilder requestBuilder =
                delete(orderUrl + "/" + order1.getUuid().toString());

        mockMvc.perform(requestBuilder).andExpect(status().isNoContent()).andReturn();

        verify(getAllOrdersForCompanyUseCase, never()).getAllOrdersByCompanyId(any());
        verify(createOrderForCompanyUsecase, never()).createOrder(any());
        verify(updateOrderOfCompanyUseCase, never()).updateOrder(any());
        verify(deleteOrderOfCompanyUseCase)
                .deleteOrderOfCompany(eq(new DeleteOrderOfCompanyCommand(companyId, order1.getUuid())));
    }

    private OrderDto getOrderDtoFromOrder(Order order) {
        OrderDto orderDto = new OrderDto();
        orderDto.setTitle(order.getTitle());
        orderDto.setUuid(order.getUuid().toString());
        return orderDto;
    }
}
