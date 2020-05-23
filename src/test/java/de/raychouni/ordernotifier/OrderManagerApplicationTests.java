package de.raychouni.ordernotifier;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.raychouni.ordernotifier.entities.Order;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrderManagerApplicationTests {

    @LocalServerPort
    private int port;

    private String url;

    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        url = "http://localhost:" + port + "/orders";
    }

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @Sql({"classpath:company_test.sql", "classpath:order_test.sql"})
    void loadOrders() throws IOException {
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        assertEquals(200, response.getStatusCodeValue());
        String body = response.getBody();
        JsonNode jsonNode = objectMapper.readTree(body.getBytes());
        JsonNode orders = jsonNode.get("_embedded").get("orders");
        assertEquals(1, orders.size());
        Order order = objectMapper.readValue(orders.get(0).toString(), Order.class);
    }

}
