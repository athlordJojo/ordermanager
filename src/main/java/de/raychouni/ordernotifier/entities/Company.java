package de.raychouni.ordernotifier.entities;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@Table(name = "company")
public class Company {
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "BINARY(16)")
    @Id
    private UUID uuid;

    @Column
    private String name;

    @Column
    private String description;


    @OneToMany(
            mappedBy = "company",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Order> orders;


    public void addOrder(Order order){
        orders.add(order);
        order.setCompany(this);
    }

    public void deleteOrder(Order order){
        orders.remove(order);
        order.setCompany(null);
    }
}
