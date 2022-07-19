package com.technoelevate.redis.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.technoelevate.redis.entity.Department;
import com.technoelevate.redis.entity.Employee;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

	public List<Employee> findByEmpIdIn(List<Long> empIds);
	
	public Employee findByEmpId(Long empIds);
	
	public List<Employee> findByDepartment(Department department);

	public List<Employee> findByDepartmentIn(List<Department> departments);

}
