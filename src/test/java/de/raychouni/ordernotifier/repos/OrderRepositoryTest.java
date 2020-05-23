package de.raychouni.ordernotifier.repos;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class OrderRepositoryTest {

    @Autowired
    OrderRepository orderRepository;

    @Test
    @Sql({"classpath:company_test.sql", "classpath:order_test.sql"})
    void findAll() {
        assertEquals(1, orderRepository.findAll().size());
    }
}