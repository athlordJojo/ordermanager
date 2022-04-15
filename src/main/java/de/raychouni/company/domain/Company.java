package de.raychouni.company.domain;

import de.raychouni.order.adapter.out.persistence.entities.OrderJPA;
import java.util.List;
import java.util.UUID;
import lombok.Data;

@Data
public class Company {
    private UUID uuid;
    private String name;
    private String description;
    private List<OrderJPA> orders;
}
