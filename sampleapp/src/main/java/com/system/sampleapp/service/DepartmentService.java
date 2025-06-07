package com.system.sampleapp.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import com.system.sampleapp.model.Department;
import com.system.sampleapp.model.ViewDepartment;
import com.system.sampleapp.repo.DepartmentRepo;

import jakarta.persistence.EntityNotFoundException;

@Service
public class DepartmentService {

    @Autowired
    private DepartmentRepo repo;

    public List<Department> getDepts() {
        return repo.findAll();
    }

    public Department getDept(int id) {
        if (repo.findById(id).isEmpty()) {
            throw new EntityNotFoundException("Department not found");
        }
        return repo.findById(id).get();
    }

    public String addDept(Department department) {
        if (repo.findById(department.getId()).isPresent()) {
            throw new DuplicateKeyException("Department ID already exists: " + department.getId());
        }
        repo.save(department);
        return "New Department added";
    }

    public void deleteDept(int id) {
        Department department = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Department not found with id: " + id));
        repo.delete(department);
    }

    public Department updateDept(int id, Department updatedDepartment) {
        Department existingDept = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Department not found with id: " + id));
        existingDept.setName(updatedDepartment.getName());
        return repo.save(existingDept);
    }

    public List<String> getDepartmentNames() {
        if (repo.getDeptNames().isEmpty()) {
            throw new EntityNotFoundException("Department not found");
        }
        return repo.getDeptNames();
    }

    public List<Department> searchDeptName(String name) {
        if (repo.searchname(name).isEmpty()) {
            throw new EntityNotFoundException("Department not found");
        }
        return repo.searchname(name);
    }
    
    
    //Using the Normal Method
    public int getEmpCount(int id) {
    	if(repo.findById(id).isEmpty()) {
    		throw new EntityNotFoundException("Department Not Found");
    	}
    	return repo.numberOfEMp(id);
    }
    
    //Using View 
    public ViewDepartment getEmpCountView(int id) {
    	if (repo.findById(id).isEmpty()) {
    		throw new EntityNotFoundException("Department Not FOund");
    	}
    	Department department= repo.findById(id).get();
    	ViewDepartment viewDepartment = new ViewDepartment(department.getId(),department.getName(),department.getEstablished(),getEmpCount(id));
    	return viewDepartment;
    }
    
    
}
