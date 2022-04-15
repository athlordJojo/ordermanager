package de.raychouni.order.adapter.in.web.dtos;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderDto {
    private String uuid;
    private String title;
    private int scoreBoardNumber;
    private String state;
    private Date createdDate;
    private Date lastModifiedDate;
}
