package com.technoelevate.redis.service;

import java.util.List;

import com.technoelevate.redis.dto.EmployeeDto;

public interface EmployeeService {

	public EmployeeDto create(EmployeeDto employeeDto) ;
	
	public EmployeeDto update(EmployeeDto employeeDto) ;

	public EmployeeDto getEmployee(Long empId);

	public boolean delete(Long empId) ;

	public List<EmployeeDto> getAllEmployee() ;

}
