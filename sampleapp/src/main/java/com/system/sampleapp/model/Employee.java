package com.system.sampleapp.model;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;

@Entity
public class Employee extends Person{
	@Id
	private String empNo;
	private double salary;
	
	@ManyToOne
	private Department department;
	
	@ManyToMany(mappedBy ="employees" )
	private List<Project>projet;
	
	
	
	public Employee(String empNo, double salary, Department department, List<Project> projet) {
		super();
		this.empNo = empNo;
		this.salary = salary;
		this.department = department;
		this.projet = projet;
	}

	public Employee() {
		
	}

	public String getEmpNo() {
		return empNo;
	}

	public void setEmpNo(String empNo) {
		this.empNo = empNo;
	}



	public double getSalary() {
		return salary;
	}

	public void setSalary(double salary) {
		this.salary = salary;
	}

	public Department getDepartment() {
		return department;
	}

	public void setDepartment(Department department) {
		this.department = department;
	}

	public List<Project> getProjet() {
		return projet;
	}

	public void setProjet(List<Project> projet) {
		this.projet = projet;
	}
	
	
}
