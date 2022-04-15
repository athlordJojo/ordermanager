package de.raychouni.order.adapter.out.persistence;

import static de.raychouni.order.adapter.out.persistence.entities.OrderJPA.State.IN_PROGRESS;
import static org.junit.jupiter.api.Assertions.*;

import de.raychouni.company.adapter.out.persistence.CompanyRepository;
import de.raychouni.company.adapter.out.persistence.entities.CompanyJPA;
import de.raychouni.configuration.JPAConfiguration;
import de.raychouni.order.adapter.out.persistence.entities.OrderJPA;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@Import(JPAConfiguration.class)
class OrderJPARepositoryTest {

    @Autowired OrderRepository orderRepository;

    @Autowired CompanyRepository companyRepository;

    private final UUID companyId = UUID.fromString("B28C343D-03C1-4FF1-90B9-5DDA8AFD3BFE");
    private final UUID companyWithoutOrderId =
            UUID.fromString("AA09E3B5-1959-4C92-BCED-C643AC50883A");
    private final UUID order1Id = UUID.fromString("CC94F0AB-57CC-4D3B-BA9C-D3861CF4A541");

    @Test
    @Sql({"classpath:company_test.sql", "classpath:order_test.sql"})
    void findAll() {
        assertEquals(1, orderRepository.findAll().size());
    }

    @Test
    @Sql({
        "classpath:company_test.sql",
        "classpath:additional_company_test.sql",
        "classpath:order_test.sql"
    })
    void findAllByCompany_Uuid_expectOneOrder() {
        assertEquals(1, orderRepository.findAllByCompany_Uuid(companyId).size());
    }

    @Test
    @Sql({
        "classpath:company_test.sql",
        "classpath:additional_company_test.sql",
        "classpath:order_test.sql"
    })
    void findAllByCompany_Uuid_expectNoOrder() {
        assertTrue(orderRepository.findAllByCompany_Uuid(companyWithoutOrderId).isEmpty());
    }

    @Test
    @Sql({
        "classpath:company_test.sql",
        "classpath:additional_company_test.sql",
        "classpath:order_test.sql"
    })
    void findFirstByUuidAndCompanyUuid_withValidMapping_expectResult() {
        Optional<OrderJPA> result = orderRepository.findFirstByUuidAndCompany_Uuid(order1Id, companyId);
        assertTrue(result.isPresent());
        OrderJPA order = result.get();
        assertEquals(order1Id, order.getUuid());
    }

    @Test
    @Sql({
        "classpath:company_test.sql",
        "classpath:additional_company_test.sql",
        "classpath:order_test.sql"
    })
    void findFirstByUuidAndCompanyUuid_withNonExistingMapping_expectEmptyResult() {
        Optional<OrderJPA> result =
                orderRepository.findFirstByUuidAndCompany_Uuid(order1Id, companyWithoutOrderId);
        assertTrue(result.isEmpty());
    }

    @Test
    @Sql({"classpath:company_test.sql", "classpath:order_test.sql"})
    void checkConstraintCompanyUUid_ScoreBoardnumber() {
        Optional<OrderJPA> result = orderRepository.findFirstByUuidAndCompany_Uuid(order1Id, companyId);
        // create order with same company and same scoreboardnumber
        OrderJPA order = new OrderJPA();
        OrderJPA existingOrder = result.get();
        CompanyJPA company = existingOrder.getCompany();
        order.setCompany(company);
        order.setScoreBoardNumber(existingOrder.getScoreBoardNumber());
        order.setState(IN_PROGRESS);
        assertThrows(
                DataIntegrityViolationException.class,
                () -> {
                    orderRepository.saveAndFlush(order);
                });
    }

    @Test
    @Sql({"classpath:company_test.sql", "classpath:order_test.sql"})
    void save() {
        // create order with same company and same scoreboardnumber
        OrderJPA order = new OrderJPA();
        assertNull(order.getCreatedDate());
        assertNull(order.getLastModifiedDate());
        order.setState(IN_PROGRESS);
        order.setScoreBoardNumber(2);
        order.setTitle("Title");
        CompanyJPA company = companyRepository.findById(companyId).get();
        order.setCompany(company);

        OrderJPA savedOrder = orderRepository.saveAndFlush(order);

        // make sure these dates are set
        assertNotNull(savedOrder.getLastModifiedDate());
        assertNotNull(savedOrder.getCreatedDate());

        assertNotNull(savedOrder.getUuid());
        assertEquals(IN_PROGRESS, savedOrder.getState());
        assertEquals(2, savedOrder.getScoreBoardNumber());
        assertEquals("Title", savedOrder.getTitle());
        assertEquals(company, savedOrder.getCompany());
    }
}
