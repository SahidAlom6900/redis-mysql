package com.technoelevate.redis.service;

import java.util.List;

import com.technoelevate.redis.dto.DepartmentDto;

public interface DepartmentService {

	public DepartmentDto create(DepartmentDto departmentDto);
	
	public DepartmentDto update(DepartmentDto departmentDto);
	
	public DepartmentDto delete(Long deptId) ;
	
	public DepartmentDto getDepartment(Long deptId);
	
	public List<DepartmentDto> getAllDepartment();

}
