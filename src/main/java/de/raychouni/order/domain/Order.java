package de.raychouni.order.domain;

import de.raychouni.company.adapter.out.persistence.entities.CompanyJPA;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
public class Order {
    private UUID uuid;
    private String name;
    private String description;
    private int scoreBoardNumber;
    private State state;
    private String title;
    private Date createdDate;
    private Date lastModifiedDate;
    private CompanyJPA company;

    public enum State {
        IN_PROGRESS,
        READY
    }
}
