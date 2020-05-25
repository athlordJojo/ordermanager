package de.raychouni.ordernotifier.entities;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
@Data
@Table(name = "order_table")
@EntityListeners(AuditingEntityListener.class)
public class Order {

    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "BINARY(16)")
    @Id
    private UUID uuid;

    @Column
    private String title;

    @Column(nullable = false, name = "scoreboardnumber")
    private int scoreBoardNumber;

    @Enumerated(EnumType.STRING)
    @Column(length = 11, nullable = false)
    private State state;

    @CreatedDate
    @Column
    private Date createdDate;

    @LastModifiedDate
    @Column
    private Date lastModifiedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    private Company company;


    public enum State {
        IN_PROGRESS,
        READY,
        DONE
    }

}
