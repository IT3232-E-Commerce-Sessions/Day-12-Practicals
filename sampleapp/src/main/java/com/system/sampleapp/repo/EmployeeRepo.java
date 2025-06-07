package com.system.sampleapp.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.system.sampleapp.model.Employee;

@Repository
public interface EmployeeRepo extends JpaRepository<Employee, String> {
	
//    @Query("select e from Employee e where e.salary between ?1 and ?2")
//	public List<Employee>findSalaryRange(int b,int e);
	
//	@Query("select e from employee e where e.department.id = ?1 '")
//	public List<Employee>getfromDeptID(int did);
}
