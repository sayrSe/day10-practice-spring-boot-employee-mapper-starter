package com.afs.restapi.service.mapper;

import com.afs.restapi.entity.Company;
import com.afs.restapi.service.dto.CompanyRequest;
import com.afs.restapi.service.dto.CompanyResponse;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.Optional;

public class CompanyMapper {

    private CompanyMapper() {
    }

    public static Company toEntity(CompanyRequest companyRequest) {
        Company company = new Company();
        BeanUtils.copyProperties(companyRequest, company);
        return company;
    }

    public static CompanyResponse toResponse(Company company) {
        CompanyResponse companyResponse = new CompanyResponse();
        BeanUtils.copyProperties(company, companyResponse);
        companyResponse.setEmployeesCount(getEmployeesCount(company));
        return companyResponse;
    }

    private static int getEmployeesCount(Company company) {
        return Optional.ofNullable(company.getEmployees())
                .map(List::size)
                .orElse(0);
    }
}
