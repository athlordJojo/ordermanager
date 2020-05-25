package de.raychouni.ordernotifier;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.raychouni.ordernotifier.dtos.CompanyDto;
import de.raychouni.ordernotifier.dtos.OrderDto;
import de.raychouni.ordernotifier.repos.CompanyRepository;
import de.raychouni.ordernotifier.repos.OrderRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.util.Collections;
import java.util.Objects;
import java.util.UUID;

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

    private String url;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    CompanyRepository companyRepository;

    @BeforeEach
    void setUp() {
        url = "http://localhost:" + port;
//         for making patch requests: https://stackoverflow.com/questions/29447382/resttemplate-patch-request
//        restTemplate.getRestTemplate().setRequestFactory(new HttpComponentsClientHttpRequestFactory());
    }

    @AfterEach
    void tearDown() {
        orderRepository.deleteAll();
        companyRepository.deleteAll();
    }

    @Autowired
    ObjectMapper objectMapper;

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
    @Sql({"classpath:company_test.sql"})
    void createOrdersForCompany() {
        assertEquals(0, orderRepository.count());
        OrderDto newDto = new OrderDto();
        newDto.setState("IN_PROGRESS");
        newDto.setScoreBoardNumber(1);
        newDto.setTitle("Test title");
        ResponseEntity<OrderDto> response = restTemplate.postForEntity(url + "/companies/" + companyId + "/orders", newDto, OrderDto.class);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        OrderDto responseDto = response.getBody();
        assertNotNull(responseDto);
        assertNotNull(responseDto.getUuid());
        assertEquals(1, orderRepository.count());

        ResponseEntity<OrderDto[]> orderResponse = restTemplate.getForEntity(url + "/companies/" + companyId + "/orders", OrderDto[].class);
        assertEquals(HttpStatus.OK, orderResponse.getStatusCode());
        assertEquals(1, Objects.requireNonNull(orderResponse.getBody()).length);
    }

    @Test
    @Sql({"classpath:company_test.sql", "classpath:order_test.sql"})
    void updateOrderOfCompany() {
        OrderDto updateDto = new OrderDto();
        updateDto.setState("READY");
        updateDto.setScoreBoardNumber(2);
        updateDto.setTitle("New title");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<OrderDto> request = new HttpEntity<>(updateDto, headers);

        ResponseEntity<OrderDto> response = restTemplate.exchange(url + "/companies/" + companyId + "/orders/" + order1Id, PUT, request, OrderDto.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.hasBody());
        OrderDto body = Objects.requireNonNull(response.getBody());
        assertEquals("READY", body.getState());
        assertEquals("New title", body.getTitle());
        assertEquals(2, body.getScoreBoardNumber());
    }
}
