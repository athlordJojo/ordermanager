package de.raychouni.ordernotifier.repos;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.UUID;

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

    @Test
    @Sql({"classpath:company_test.sql", "classpath:additional_company_test.sql", "classpath:order_test.sql"})
    void findAllByCompany_Uuid_expectOneOrder() {
        UUID uuid = UUID.fromString("B28C343D-03C1-4FF1-90B9-5DDA8AFD3BFE");
        assertEquals(1, orderRepository.findAllByCompany_Uuid(uuid).size());
    }

    @Test
    @Sql({"classpath:company_test.sql", "classpath:additional_company_test.sql", "classpath:order_test.sql"})
    void findAllByCompany_Uuid_expectNoOrder() {
        UUID uuid = UUID.fromString("AA09E3B5-1959-4C92-BCED-C643AC50883A");
        assertTrue(orderRepository.findAllByCompany_Uuid(uuid).isEmpty());
    }
}