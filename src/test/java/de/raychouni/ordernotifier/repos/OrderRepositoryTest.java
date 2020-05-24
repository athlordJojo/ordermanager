package de.raychouni.ordernotifier.repos;

import de.raychouni.ordernotifier.entities.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class OrderRepositoryTest {

    @Autowired
    OrderRepository orderRepository;
    private UUID companyId = UUID.fromString("B28C343D-03C1-4FF1-90B9-5DDA8AFD3BFE");;
    private UUID companyWithoutOrderId = UUID.fromString("AA09E3B5-1959-4C92-BCED-C643AC50883A");
    private UUID order1Id = UUID.fromString("CC94F0AB-57CC-4D3B-BA9C-D3861CF4A541");

    @Test
    @Sql({"classpath:company_test.sql", "classpath:order_test.sql"})
    void findAll() {
        assertEquals(1, orderRepository.findAll().size());
    }

    @Test
    @Sql({"classpath:company_test.sql", "classpath:additional_company_test.sql", "classpath:order_test.sql"})
    void findAllByCompany_Uuid_expectOneOrder() {
        assertEquals(1, orderRepository.findAllByCompany_Uuid(companyId).size());
    }

    @Test
    @Sql({"classpath:company_test.sql", "classpath:additional_company_test.sql", "classpath:order_test.sql"})
    void findAllByCompany_Uuid_expectNoOrder() {
        assertTrue(orderRepository.findAllByCompany_Uuid(companyWithoutOrderId).isEmpty());
    }

    @Test
    @Sql({"classpath:company_test.sql", "classpath:additional_company_test.sql", "classpath:order_test.sql"})
    void findFirstByUuidAndCompanyUuid_withValidMapping_expectResult(){
        Optional<Order> result = orderRepository.findFirstByUuidAndCompany_Uuid(order1Id, companyId);
        assertTrue(result.isPresent());
        Order order = result.get();
        assertEquals(order1Id, order.getUuid());
    }

    @Test
    @Sql({"classpath:company_test.sql", "classpath:additional_company_test.sql", "classpath:order_test.sql"})
    void findFirstByUuidAndCompanyUuid_withNonExistingMapping_expectEmptyResult(){
        Optional<Order> result = orderRepository.findFirstByUuidAndCompany_Uuid(order1Id, companyWithoutOrderId);
        assertFalse(result.isPresent());
        assertFalse(result.isEmpty());
    }
}