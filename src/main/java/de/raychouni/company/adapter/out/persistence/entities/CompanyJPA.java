package de.raychouni.company.adapter.out.persistence.entities;

import de.raychouni.order.adapter.out.persistence.entities.OrderJPA;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.persistence.*;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Data
@Table(name = "company")
public class CompanyJPA {
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "BINARY(16)")
    @Id
    private UUID uuid;

    @Column private String name;

    @Column private String description;

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderJPA> orders = new ArrayList<>();

    public void addOrder(OrderJPA order) {
        orders.add(order);
        order.setCompany(this);
    }

    public void deleteOrder(OrderJPA order) {
        orders.remove(order);
        order.setCompany(null);
    }
}
