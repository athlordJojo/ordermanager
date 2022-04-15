package de.raychouni.order.domain;

import de.raychouni.company.domain.Company;
import java.util.Date;
import java.util.UUID;
import lombok.Data;

@Data
// @Accessors(fluent = true)
public class Order {
    private UUID uuid;
    private String name;
    private String description;
    private int scoreBoardNumber;
    private State state;
    private String title;
    private Date createdDate;
    private Date lastModifiedDate;
    private Company company;

    public enum State {
        IN_PROGRESS,
        READY
    }
}
