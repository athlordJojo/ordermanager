package de.raychouni.company.adapter.in.web;

import de.raychouni.company.application.port.in.GetAllCompaniesUseCase;
import de.raychouni.company.domain.Company;
import de.raychouni.company.adapter.in.web.dtos.CompanyDto;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/companies")
@CrossOrigin(origins = "http://localhost:4200")
public class CompanyController {

    private final GetAllCompaniesUseCase getAllCompaniesUseCase;
    private final ModelMapper modelMapper;

    public CompanyController(GetAllCompaniesUseCase getAllCompaniesUseCase, ModelMapper modelMapper) {
        this.getAllCompaniesUseCase = getAllCompaniesUseCase;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public List<CompanyDto> getCustomers() {
        List<Company> all = getAllCompaniesUseCase.getAllCompanies();
        return all.stream()
                .map(company -> modelMapper.map(company, CompanyDto.class))
                .collect(Collectors.toList());
    }

}
