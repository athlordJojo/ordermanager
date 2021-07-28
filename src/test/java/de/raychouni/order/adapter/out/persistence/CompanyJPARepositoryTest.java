package de.raychouni.order.adapter.out.persistence;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class CompanyJPARepositoryTest {

    @Autowired
    CompanyRepository repo;

    @Test
    @Sql({"classpath:company_test.sql"})
    void findCompany(){
        assertEquals(1, repo.findAll().size());
    }
}
