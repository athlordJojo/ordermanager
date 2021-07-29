package de.raychouni.ordernotifier;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.raychouni.ordernotifier.dtos.CompanyDto;
import de.raychouni.ordernotifier.dtos.OrderDto;
import de.raychouni.company.adapter.out.persistence.CompanyRepository;
import de.raychouni.order.adapter.out.persistence.OrderRepository;
import de.raychouni.order.domain.OrderUpdate;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static de.raychouni.order.domain.OrderUpdate.CHANGE_TYPE.*;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpMethod.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrderManagerApplicationTests {

    private final UUID companyId = UUID.fromString("B28C343D-03C1-4FF1-90B9-5DDA8AFD3BFE");
    private final UUID companyWithoutOrderId = UUID.fromString("AA09E3B5-1959-4C92-BCED-C643AC50883A");
    private final UUID order1Id = UUID.fromString("CC94F0AB-57CC-4D3B-BA9C-D3861CF4A541");

    @LocalServerPort
    private int port;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CompanyRepository companyRepository;

    private CompletableFuture<OrderUpdate> completableFuture;
    private StompSession stompSession;

    private String url;

    @BeforeEach
    void setUp() throws InterruptedException, ExecutionException, TimeoutException {
        url = "http://localhost:" + port;

        // prepeare for receiving incoming websocket messages
        List<Transport> transports = List.of(new WebSocketTransport(new StandardWebSocketClient()));
        WebSocketStompClient stompClient = new WebSocketStompClient(new SockJsClient(transports));
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        stompSession = stompClient.connect("ws://localhost:" + port + "/liveupdates", new StompSessionHandlerAdapter() {
        }).get(1, SECONDS);
        completableFuture = new CompletableFuture<>();

//         for making patch requests: https://stackoverflow.com/questions/29447382/resttemplate-patch-request
//        restTemplate.getRestTemplate().setRequestFactory(new HttpComponentsClientHttpRequestFactory());
    }

    @AfterEach
    void tearDown() {
        orderRepository.deleteAll();
        companyRepository.deleteAll();
        stompSession.disconnect();
    }

    @Test
    @Sql({"classpath:company_test.sql", "classpath:order_test.sql"})
    void loadCompanies() {
        ResponseEntity<CompanyDto[]> response = restTemplate.getForEntity(url + "/companies", CompanyDto[].class);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, Objects.requireNonNull(response.getBody()).length);
    }

    @Test
    @Sql({"classpath:company_test.sql", "classpath:order_test.sql"})
    void loadOrdersOfCompany() {
        ResponseEntity<OrderDto[]> response = restTemplate.getForEntity(url + "/companies/" + companyId + "/orders", OrderDto[].class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, Objects.requireNonNull(response.getBody()).length);
    }

    @Test
    @Sql({"classpath:company_test.sql", "classpath:order_test.sql"})
    void deleteOrderOfCompany() throws InterruptedException, ExecutionException, TimeoutException {
        assertEquals(1, orderRepository.count());

        // listen for websocket messages
        stompSession.subscribe("/topic/orders", new CreateStompFrameHandler());

        restTemplate.delete(url + "/companies/" + companyId + "/orders/" + order1Id);
        // expect
        ResponseEntity<OrderDto[]> response = restTemplate.getForEntity(url + "/companies/" + companyId + "/orders", OrderDto[].class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, Objects.requireNonNull(response.getBody()).length);
        assertEquals(0, orderRepository.count());

        waitAndCheckIncomingWebsocketMessage(DELETED);
    }

    @Test
    @Sql({"classpath:company_test.sql"})
    void createOrdersForCompany() throws InterruptedException, ExecutionException, TimeoutException {
        assertEquals(0, orderRepository.count());
        OrderDto newDto = new OrderDto();
        newDto.setState("IN_PROGRESS");
        newDto.setScoreBoardNumber(1);
        newDto.setTitle("Test title");

        // listen for websocket messages
        stompSession.subscribe("/topic/orders", new CreateStompFrameHandler());

        ResponseEntity<OrderDto> response = restTemplate.postForEntity(url + "/companies/" + companyId + "/orders", newDto, OrderDto.class);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        OrderDto responseDto = response.getBody();
        assertNotNull(responseDto);
        assertNotNull(responseDto.getUuid());
        assertEquals(1, orderRepository.count());

        ResponseEntity<OrderDto[]> orderResponse = restTemplate.getForEntity(url + "/companies/" + companyId + "/orders", OrderDto[].class);
        assertEquals(HttpStatus.OK, orderResponse.getStatusCode());
        assertEquals(1, Objects.requireNonNull(orderResponse.getBody()).length);

        waitAndCheckIncomingWebsocketMessage(INSERTED);
    }

    @Test
    @Sql({"classpath:company_test.sql", "classpath:order_test.sql"})
    void updateOrderOfCompany() throws InterruptedException, ExecutionException, TimeoutException {
        OrderDto updateDto = new OrderDto();
        updateDto.setState("READY");
        updateDto.setScoreBoardNumber(2);
        updateDto.setTitle("New title");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<OrderDto> request = new HttpEntity<>(updateDto, headers);

        // listen for websocket messages
        stompSession.subscribe("/topic/orders", new CreateStompFrameHandler());

        ResponseEntity<OrderDto> response = restTemplate.exchange(url + "/companies/" + companyId + "/orders/" + order1Id, PUT, request, OrderDto.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.hasBody());
        OrderDto body = Objects.requireNonNull(response.getBody());
        assertEquals("READY", body.getState());
        assertEquals("New title", body.getTitle());
        assertEquals(2, body.getScoreBoardNumber());
        waitAndCheckIncomingWebsocketMessage(UPDATED);
    }

    private void waitAndCheckIncomingWebsocketMessage(OrderUpdate.CHANGE_TYPE changeType) throws InterruptedException, ExecutionException, TimeoutException {
        OrderUpdate orderUpdate = completableFuture.get(5, SECONDS);
        assertNotNull(orderUpdate);
        assertEquals(changeType, orderUpdate.getChange());
    }

    private class CreateStompFrameHandler implements StompFrameHandler {
        @Override
        public Type getPayloadType(StompHeaders stompHeaders) {
            return Object.class;
        }

        @Override
        public void handleFrame(StompHeaders stompHeaders, Object o) {
            try {
                // until now couldnt find a way send messages preserving the type, therefore to string and then to required type
                byte[] contentAsByteArray = (byte[]) o;
                String h= new String(contentAsByteArray);
                OrderUpdate orderUpdate = objectMapper.readValue(h, OrderUpdate.class);
                completableFuture.complete(orderUpdate);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
    }
}
