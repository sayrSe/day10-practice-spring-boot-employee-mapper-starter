package com.afs.restapi;

import com.afs.restapi.entity.Company;
import com.afs.restapi.entity.Employee;
import com.afs.restapi.repository.CompanyRepository;
import com.afs.restapi.repository.EmployeeRepository;
import com.afs.restapi.service.dto.CompanyRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CompanyApiTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private EmployeeRepository employeeRepository;

    @BeforeEach
    void setUp() {
        companyRepository.deleteAll();
        employeeRepository.deleteAll();
    }

    @Test
    void should_find_companies() throws Exception {
        Company company = companyRepository.save(getCompanyOOCL());

        mockMvc.perform(get("/companies"))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(company.getId()))
                .andExpect(jsonPath("$[0].name").value(company.getName()))
                .andExpect(jsonPath("$[0].employeesCount").exists());
    }

    @Test
    void should_find_company_by_id() throws Exception {
        Company company = companyRepository.save(getCompanyOOCL());
        Employee employee = employeeRepository.save(getEmployee(company));

        mockMvc.perform(get("/companies/{id}", company.getId()))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.id").value(company.getId()))
                .andExpect(jsonPath("$.name").value(company.getName()))
                .andExpect(jsonPath("$.employeesCount").value(1))
                .andExpect(jsonPath("$.employees[0].id").value(1L))
                .andExpect(jsonPath("$.employees[0].name").value(employee.getName()))
                .andExpect(jsonPath("$.employees[0].age").value(employee.getAge()))
                .andExpect(jsonPath("$.employees[0].gender").value(employee.getGender()))
                .andExpect(jsonPath("$.employees[0].salary").doesNotExist());
    }

    @Test
    void should_update_company_name() throws Exception {
        Company previousCompany = companyRepository.save(new Company(null, "Facebook"));
        CompanyRequest updatedCompanyRequest = new CompanyRequest("Meta");
        ObjectMapper objectMapper = new ObjectMapper();
        String updatedCompanyRequestJSON = objectMapper.writeValueAsString(updatedCompanyRequest);
        mockMvc.perform(put("/companies/{id}", previousCompany.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedCompanyRequestJSON))
                .andExpect(status().is(204));

        Optional<Company> optionalCompany = companyRepository.findById(previousCompany.getId());
        assertTrue(optionalCompany.isPresent());
        Company updatedCompany = optionalCompany.get();
        assertEquals(previousCompany.getId(), updatedCompany.getId());
        assertEquals(updatedCompanyRequest.getName(), updatedCompany.getName());
    }

    @Test
    void should_delete_company_name() throws Exception {
        Company company = companyRepository.save(getCompanyGoogle());
        mockMvc.perform(delete("/companies/{id}", company.getId()))
                .andExpect(status().is(204));

        assertTrue(companyRepository.findById(company.getId()).isEmpty());
    }

    @Test
    void should_create_company() throws Exception {
        CompanyRequest companyRequest = new CompanyRequest("OOCL");

        ObjectMapper objectMapper = new ObjectMapper();
        String companyRequestJSON = objectMapper.writeValueAsString(companyRequest);
        mockMvc.perform(post("/companies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(companyRequestJSON))
                .andExpect(status().is(201))
                .andExpect(jsonPath("$.id").value(notNullValue()))
                .andExpect(jsonPath("$.name").value(companyRequest.getName()))
                .andExpect(jsonPath("$.employeesCount").exists());
    }

    @Test
    void should_find_companies_by_page() throws Exception {
        Company oocl = companyRepository.save(getCompanyOOCL());
        Company thoughtworks = companyRepository.save(getCompanyThoughtWorks());
        companyRepository.save(getCompanyGoogle());

        mockMvc.perform(get("/companies")
                        .param("pageNumber", "1")
                        .param("pageSize", "2"))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(oocl.getId()))
                .andExpect(jsonPath("$[0].name").value(oocl.getName()))
                .andExpect(jsonPath("$[0].employeesCount").exists())
                .andExpect(jsonPath("$[1].id").value(thoughtworks.getId()))
                .andExpect(jsonPath("$[1].name").value(thoughtworks.getName()))
                .andExpect(jsonPath("$[1].employeesCount").exists());
    }

    @Test
    void should_find_employees_by_companies() throws Exception {
        Company oocl = companyRepository.save(getCompanyOOCL());
        Employee employee = employeeRepository.save(getEmployee(oocl));

        mockMvc.perform(get("/companies/{companyId}/employees", oocl.getId()))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(employee.getId()))
                .andExpect(jsonPath("$[0].name").value(employee.getName()))
                .andExpect(jsonPath("$[0].age").value(employee.getAge()))
                .andExpect(jsonPath("$[0].gender").value(employee.getGender()))
                .andExpect(jsonPath("$[0].salary").value(employee.getSalary()));
    }

    private static Employee getEmployee(Company company) {
        Employee employee = new Employee();
        employee.setName("Bob");
        employee.setAge(22);
        employee.setGender("Male");
        employee.setSalary(10000);
        employee.setCompanyId(company.getId());
        return employee;
    }


    private static Company getCompanyOOCL() {
        Company company = new Company();
        company.setName("OOCL");
        return company;
    }

    private static Company getCompanyThoughtWorks() {
        Company company = new Company();
        company.setName("Thoughtworks");
        return company;
    }

    private static Company getCompanyGoogle() {
        Company company = new Company();
        company.setName("Google");
        return company;
    }
}