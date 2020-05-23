package de.raychouni.ordernotifier.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

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
