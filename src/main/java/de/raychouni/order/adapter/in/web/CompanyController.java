package de.raychouni.order.adapter.in.web;

import de.raychouni.ordernotifier.dtos.CompanyDto;
import de.raychouni.order.adapter.out.persistence.entities.CompanyJPA;
import de.raychouni.ordernotifier.services.CompanyService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/companies")
@CrossOrigin(origins = "http://localhost:4200")
public class CompanyController {

    private final CompanyService companyService;
    private final ModelMapper modelMapper;

    public CompanyController(CompanyService companyService, ModelMapper modelMapper) {
        this.companyService = companyService;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public List<CompanyDto> getCustomers() {
        List<CompanyJPA> all = companyService.getAll();
        return all.stream()
                .map(company -> modelMapper.map(company, CompanyDto.class))
                .collect(Collectors.toList());
    }

}
