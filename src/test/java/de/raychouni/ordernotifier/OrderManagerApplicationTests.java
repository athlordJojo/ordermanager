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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrderManagerApplicationTests {

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
        ResponseEntity<OrderDto[]> response = restTemplate.getForEntity(url + "/companies/B28C343D-03C1-4FF1-90B9-5DDA8AFD3BFE/orders", OrderDto[].class);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, Objects.requireNonNull(response.getBody()).length);
    }

    @Test
    @Sql({"classpath:company_test.sql"})
    void createOrdersForCompany() {
        OrderDto dto = new OrderDto();
        dto.setState("IN_PROGRESS");
        dto.setScoreBoardNumber(1);
        dto.setTitle("Test title");
        ResponseEntity<OrderDto> response = restTemplate.postForEntity(url + "/companies/B28C343D-03C1-4FF1-90B9-5DDA8AFD3BFE/orders", dto, OrderDto.class);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        OrderDto responseDto = response.getBody();
        assertNotNull(responseDto);
        assertNotNull(responseDto.getUuid());
    }
}
