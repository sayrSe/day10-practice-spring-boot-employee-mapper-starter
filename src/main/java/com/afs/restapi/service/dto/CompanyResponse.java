package com.afs.restapi.service.dto;

import com.afs.restapi.entity.Employee;

import java.util.List;

public class CompanyResponse {

    private Long id;
    private String name;
    private int employeesCount;
    private List<Employee> employees;

    public CompanyResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
    }

    public int getEmployeesCount() {
        return employeesCount;
    }

    public void setEmployeesCount(int employeesCount) {
        this.employeesCount = employeesCount;
    }
}
