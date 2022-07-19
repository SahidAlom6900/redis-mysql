package com.technoelevate.redis.service;

import java.util.Collections;
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
@AllArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

	private DepartmentRepository departmentRepository;

	private EmployeeRepository employeeRepository;

//	@Caching(evict = { @CacheEvict(value = "employee", allEntries = true) })
	@Cacheable(value = "department", key = "#departmentDto.deptName")
	public DepartmentDto create(DepartmentDto departmentDto) {
		List<Long> empIds = departmentDto.getEmployeeDtos() == null ? Collections.emptyList()
				: departmentDto.getEmployeeDtos().stream().filter(emp -> emp.getEmpId() != null)
						.map(EmployeeDto::getEmpId).collect(Collectors.toList());

		List<Employee> employees = empIds.isEmpty() ? Collections.emptyList()
				: employeeRepository.findByEmpIdIn(empIds);

		Department department = departmentDto.getDeptId() == null
				? Department.builder().deptName(departmentDto.getDeptName()).build()
				: departmentRepository.findById(departmentDto.getDeptId())
						.orElse(Department.builder().deptName(departmentDto.getDeptName()).build());
		if (!employees.isEmpty())
			department.setEmployee(employees);

		Department department2 = departmentRepository.save(department);
		employees.stream().forEach(emp -> emp.setDepartment(department2));
		List<Employee> employeeList = employeeRepository.saveAll(employees);

		List<EmployeeDto> employeeDtos = employeeList.stream().map(emp -> EmployeeDto.builder()
				.firstName(emp.getFirstName()).lastName(emp.getLastName()).gender(emp.getGender()).build())
				.collect(Collectors.toList());

		return DepartmentDto.builder().deptName(department2.getDeptName()).employeeDtos(employeeDtos).build();
	}

	@Caching(evict = { @CacheEvict(value = "department", allEntries = true) }, put = {
			@CachePut(value = "department", key = "#departmentDto.deptId") })
	public DepartmentDto update(DepartmentDto departmentDto) {

		Department department = departmentRepository.findById(departmentDto.getDeptId())
				.orElse(Department.builder().deptName(departmentDto.getDeptName()).build());
		List<Employee> employees = departmentDto.getEmployeeDtos().stream()
				.map(employee -> employeeRepository.findByEmpId(employee.getEmpId())).filter(emp -> emp != null)
				.map(emp -> {
					emp.setDepartment(department);
					return emp;
				}).collect(Collectors.toList());
		department.setEmployee(employees);
		if (departmentDto.getDeptName() == null)
			departmentDto.setDeptName(department.getDeptName());
		BeanUtils.copyProperties(departmentDto, department);
		Department dept = departmentRepository.save(department);
		return getDept(dept, employees);
	}

	private DepartmentDto getDept(Department department, List<Employee> employees) {
		EmployeeDto employeeDto = EmployeeDto.builder().build();
		DepartmentDto departmentDto = DepartmentDto.builder().build();
		if (department != null) {
			if (!employees.isEmpty()) {
				List<EmployeeDto> empList = employees.stream().filter(emp1 -> emp1.getDepartment() != null)
						.filter(emp -> emp.getDepartment().getDeptId().equals(department.getDeptId())).map(employee -> {
							BeanUtils.copyProperties(employee, employeeDto);
							return employeeDto;
						}).collect(Collectors.toList());
				departmentDto.setEmployeeDtos(empList);
			}
			BeanUtils.copyProperties(department, departmentDto);
		}
		return departmentDto;
	}

	@Caching(evict = { @CacheEvict(value = "department", key = "#deptId"),
			@CacheEvict(value = "department", allEntries = true) })
	public DepartmentDto delete(Long deptId) {
		Department department = departmentRepository.findById(deptId).orElseThrow(IllegalStateException::new);

		List<Employee> employees = employeeRepository.findByDepartment(department).stream().map(emp -> {
			emp.setDepartment(null);
			return emp;
		}).collect(Collectors.toList());

		employeeRepository.saveAll(employees);

		departmentRepository.delete(department);

		return getDept(department, employees);
	}

	@Cacheable(value = "department", key = "#deptId")
	public DepartmentDto getDepartment(Long deptId) {
		Department department = departmentRepository.findById(deptId).orElseThrow(IllegalStateException::new);
		return getDept(department, department.getEmployee());
	}

	@Cacheable(value = "departments")
	public List<DepartmentDto> getAllDepartment() {
		List<Department> departments = departmentRepository.findAll();
		List<Employee> emp = employeeRepository.findByDepartmentIn(departments);
		return departments.isEmpty() ? Collections.emptyList()
				: departments.stream().map(dept -> getDept(dept, emp)).collect(Collectors.toList());
	}

}
