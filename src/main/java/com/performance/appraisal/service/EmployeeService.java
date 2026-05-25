package com.performance.appraisal.service;

import com.performance.appraisal.dto.EmployeeDTO;
import com.performance.appraisal.entity.EmpEmployee;

import java.util.List;

public interface EmployeeService {

    EmpEmployee getById(Long id);

    EmployeeDTO getDetailById(Long id);

    EmpEmployee getByEmpNo(String empNo);

    List<EmployeeDTO> list(String empName, Long deptId, String roleCode);

    void add(EmployeeDTO dto);

    void update(EmployeeDTO dto);

    void delete(Long id);

    List<EmpEmployee> listSubordinates(Long supervisorId);
}
