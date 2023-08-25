package com.afs.restapi.controller;

import com.afs.restapi.service.EmployeeService;
import com.afs.restapi.service.dto.EmployeeRequest;
import com.afs.restapi.service.dto.EmployeeResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    public List<EmployeeResponse> getAllEmployees() {
        return employeeService.findAll();
    }

    @GetMapping("/{id}")
    public EmployeeResponse getEmployeeById(@PathVariable Long id) {
        return employeeService.findById(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateEmployee(@PathVariable Long id, @RequestBody EmployeeRequest employeeRequest) {
        employeeService.update(id, employeeRequest);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEmployee(@PathVariable Long id) {
        employeeService.delete(id);
    }

    @GetMapping(params = "gender")
    public List<EmployeeResponse> getEmployeesByGender(@RequestParam String gender) {
        return employeeService.findAllByGender(gender);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EmployeeResponse createEmployee(@RequestBody EmployeeRequest employeeRequest) {
        return employeeService.create(employeeRequest);
    }

    @GetMapping(params = {"pageNumber", "pageSize"})
    public List<EmployeeResponse> findEmployeesByPage(@RequestParam Integer pageNumber, @RequestParam Integer pageSize) {
        return employeeService.findByPage(pageNumber, pageSize);
    }

}
