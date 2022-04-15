package de.raychouni.order.adapter.out.persistence.entities;

import de.raychouni.company.adapter.out.persistence.entities.CompanyJPA;
import java.util.Date;
import java.util.UUID;
import javax.persistence.*;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Data
@Table(
        name = "order_table",
        uniqueConstraints = @UniqueConstraint(columnNames = {"company_uuid", "scoreBoardNumber"}))
@EntityListeners(AuditingEntityListener.class)
public class OrderJPA {

    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "BINARY(16)")
    @Id
    private UUID uuid;

    @Column private String title;

    @Column(nullable = false, name = "scoreboardnumber")
    private int scoreBoardNumber;

    @Enumerated(EnumType.STRING)
    @Column(length = 11, nullable = false)
    private State state;

    @CreatedDate @Column private Date createdDate;

    @LastModifiedDate @Column private Date lastModifiedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    private CompanyJPA company;

    public enum State {
        IN_PROGRESS,
        READY
    }
}
