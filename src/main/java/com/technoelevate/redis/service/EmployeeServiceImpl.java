package com.technoelevate.redis.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import com.technoelevate.redis.dto.DepartmentDto;
import com.technoelevate.redis.dto.EmployeeDto;
import com.technoelevate.redis.entity.Department;
import com.technoelevate.redis.entity.Employee;
import com.technoelevate.redis.repository.DepartmentRepository;
import com.technoelevate.redis.repository.EmployeeRepository;

import lombok.AllArgsConstructor;

@Service
//@RequiredArgsConstructor
@AllArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

	private EmployeeRepository employeeRepository;

	private DepartmentRepository departmentRepository;

//	@Caching(evict = { @CacheEvict(value = "employee", allEntries = true) })
	@Cacheable(value = "employee", key = "#employeeDto.firstName")
	public EmployeeDto create(EmployeeDto employeeDto) {

		Department department = departmentRepository
				.findById(
						(employeeDto.getDepartment() == null) ? 0 : employeeDto.getDepartment().getDeptId())
				.orElse(null);
		
		Employee employee = Employee.builder().firstName(employeeDto.getFirstName()).lastName(employeeDto.getLastName())
				.gender(employeeDto.getGender()).department(department).build();
		
		Employee employee2 = employeeRepository.save(employee);

		return getEmp(employee2);

	}

	@Caching(evict = { @CacheEvict(value = "employee", allEntries = true) }, put = {
			@CachePut(value = "employee", key = "#employeeDto.empId") })
	public EmployeeDto update(EmployeeDto employeeDto) {
		Employee employee = employeeRepository.findById(employeeDto.getEmpId()).orElseThrow(IllegalStateException::new);

		Department department2 = employee.getDepartment() == null
				? Department.builder().deptId(employeeDto.getDepartment().getDeptId()).build()
				: employee.getDepartment();

		Department department = departmentRepository.findById(department2.getDeptId())
				.orElseThrow(IllegalStateException::new);

		BeanUtils.copyProperties(employeeDto, employee);

		BeanUtils.copyProperties(employeeDto.getDepartment(), department);

		employee.setDepartment(department);
		Employee employee2 = employeeRepository.save(employee);
		BeanUtils.copyProperties(employee2, employeeDto);
		return employeeDto;
	}

	@Cacheable(value = "employee", key = "#empId")
	public EmployeeDto getEmployee(Long empId) {
		Employee employee = employeeRepository.findByEmpId(empId);
		return getEmp(employee);
	}

	private EmployeeDto getEmp(Employee employee) {
		EmployeeDto employeeDto = EmployeeDto.builder().build();
		DepartmentDto departmentDto = DepartmentDto.builder().build();
		if (employee != null) {
			if (employee.getDepartment() != null)
				BeanUtils.copyProperties(employee.getDepartment(), departmentDto);
			BeanUtils.copyProperties(employee, employeeDto);
		}
		employeeDto.setDepartment(departmentDto);
		return employeeDto;
	}

	@Caching(evict = { @CacheEvict(value = "employee", key = "#empId"),
			@CacheEvict(value = "employee", allEntries = true) })
	public boolean delete(Long empId) {
		Employee employee = employeeRepository.findById(empId).orElseThrow(IllegalStateException::new);
		employeeRepository.delete(employee);
		return true;
	}

	@Cacheable(value = "employees")
	public List<EmployeeDto> getAllEmployee() {
		List<Employee> emps = employeeRepository.findAll();
		return emps.stream().map(emp -> getEmp(emp)).collect(Collectors.toList());
	}

}
