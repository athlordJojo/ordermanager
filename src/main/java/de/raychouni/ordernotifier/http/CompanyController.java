package de.raychouni.ordernotifier.http;

import de.raychouni.ordernotifier.dtos.CompanyDto;
import de.raychouni.ordernotifier.entities.Company;
import de.raychouni.ordernotifier.services.CompanyService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/companies")
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
        List<Company> all = companyService.getAll();
        List<CompanyDto> dtos = all.stream()
                .map(company -> modelMapper.map(company, CompanyDto.class))
                .collect(Collectors.toList());
        return dtos;
    }

}
