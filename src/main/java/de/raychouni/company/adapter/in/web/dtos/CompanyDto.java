package de.raychouni.company.adapter.in.web.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CompanyDto {
    private UUID uuid;
    private String name;
    private String description;
}
