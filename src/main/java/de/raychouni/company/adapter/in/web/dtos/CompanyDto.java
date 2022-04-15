package de.raychouni.company.adapter.in.web.dtos;

import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompanyDto {
    private UUID uuid;
    private String name;
    private String description;
}
