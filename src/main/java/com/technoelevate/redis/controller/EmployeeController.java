package com.technoelevate.redis.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.technoelevate.redis.dto.EmployeeDto;
import com.technoelevate.redis.response.EmployeeResponse;
import com.technoelevate.redis.service.EmployeeService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping(value = "/api/v1")
@AllArgsConstructor
public class EmployeeController {

	private final EmployeeService employeeService;

	@PostMapping("employee")
	public ResponseEntity<EmployeeResponse> createEmployee(@RequestBody EmployeeDto employeeDto) {
		return ResponseEntity.ok().body(
				EmployeeResponse.builder().error(false).message("").data(employeeService.create(employeeDto)).build());

	}

	@GetMapping("employee/{empId}")
	public ResponseEntity<EmployeeResponse> getEmployee(@PathVariable(name = "empId") Long empId) {
		return ResponseEntity.ok().body(
				EmployeeResponse.builder().error(false).message("").data(employeeService.getEmployee(empId)).build());

	}

	@PutMapping("employee")
	public ResponseEntity<EmployeeResponse> updateEmployee(
			@RequestBody EmployeeDto employeeDto) {
		return ResponseEntity.ok().body(EmployeeResponse.builder().error(false).message("")
				.data(employeeService.update( employeeDto)).build());
	}

	@DeleteMapping(value = "/employee/{empId}")
	public ResponseEntity<EmployeeResponse> deleteEmployee(@PathVariable(name = "empId") Long empId) {
		return ResponseEntity.ok()
				.body(EmployeeResponse.builder().error(false).message("").data(employeeService.delete(empId)).build());
	}

	@GetMapping(value = "/employees")
	public ResponseEntity<EmployeeResponse> getAllEmployee() {
		return ResponseEntity.ok().body(
				EmployeeResponse.builder().error(false).message("").data(employeeService.getAllEmployee()).build());
	}

}
