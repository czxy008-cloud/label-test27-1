package com.performance.appraisal.service.impl;

import com.performance.appraisal.common.ResultCode;
import com.performance.appraisal.dto.EmployeeDTO;
import com.performance.appraisal.entity.EmpEmployee;
import com.performance.appraisal.mapper.EmployeeMapper;
import com.performance.appraisal.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public EmpEmployee getById(Long id) {
        EmpEmployee employee = employeeMapper.selectById(id);
        if (employee == null) {
            throw new RuntimeException(ResultCode.EMPLOYEE_NOT_FOUND.getMessage());
        }
        return employee;
    }

    @Override
    public EmployeeDTO getDetailById(Long id) {
        EmployeeDTO dto = employeeMapper.selectDetailById(id);
        if (dto == null) {
            throw new RuntimeException(ResultCode.EMPLOYEE_NOT_FOUND.getMessage());
        }
        return dto;
    }

    @Override
    public EmpEmployee getByEmpNo(String empNo) {
        return employeeMapper.selectByEmpNo(empNo);
    }

    @Override
    public List<EmployeeDTO> list(String empName, Long deptId, String roleCode) {
        return employeeMapper.selectList(empName, deptId, roleCode);
    }

    @Override
    public void add(EmployeeDTO dto) {
        EmpEmployee existing = employeeMapper.selectByEmpNo(dto.getEmpNo());
        if (existing != null) {
            throw new RuntimeException(ResultCode.DUPLICATE_EMP_NO.getMessage());
        }
        EmpEmployee employee = new EmpEmployee();
        employee.setEmpNo(dto.getEmpNo());
        employee.setEmpName(dto.getEmpName());
        employee.setPassword(passwordEncoder.encode("123456"));
        employee.setGender(dto.getGender());
        employee.setPhone(dto.getPhone());
        employee.setEmail(dto.getEmail());
        employee.setDeptId(dto.getDeptId());
        employee.setPosition(dto.getPosition());
        employee.setJobLevel(dto.getJobLevel());
        employee.setHireDate(dto.getHireDate());
        employee.setEmpType(dto.getEmpType());
        employee.setSupervisorId(dto.getSupervisorId());
        employee.setRoleCode(dto.getRoleCode());
        employee.setStatus(1);
        employeeMapper.insert(employee);
    }

    @Override
    public void update(EmployeeDTO dto) {
        EmpEmployee employee = employeeMapper.selectById(dto.getId());
        if (employee == null) {
            throw new RuntimeException(ResultCode.EMPLOYEE_NOT_FOUND.getMessage());
        }
        if (dto.getEmpName() != null) {
            employee.setEmpName(dto.getEmpName());
        }
        if (dto.getGender() != null) {
            employee.setGender(dto.getGender());
        }
        if (dto.getPhone() != null) {
            employee.setPhone(dto.getPhone());
        }
        if (dto.getEmail() != null) {
            employee.setEmail(dto.getEmail());
        }
        if (dto.getDeptId() != null) {
            employee.setDeptId(dto.getDeptId());
        }
        if (dto.getPosition() != null) {
            employee.setPosition(dto.getPosition());
        }
        if (dto.getJobLevel() != null) {
            employee.setJobLevel(dto.getJobLevel());
        }
        if (dto.getHireDate() != null) {
            employee.setHireDate(dto.getHireDate());
        }
        if (dto.getEmpType() != null) {
            employee.setEmpType(dto.getEmpType());
        }
        if (dto.getSupervisorId() != null) {
            employee.setSupervisorId(dto.getSupervisorId());
        }
        if (dto.getRoleCode() != null) {
            employee.setRoleCode(dto.getRoleCode());
        }
        employeeMapper.update(employee);
    }

    @Override
    public void delete(Long id) {
        EmpEmployee employee = employeeMapper.selectById(id);
        if (employee == null) {
            throw new RuntimeException(ResultCode.EMPLOYEE_NOT_FOUND.getMessage());
        }
        employeeMapper.deleteById(id);
    }

    @Override
    public List<EmpEmployee> listSubordinates(Long supervisorId) {
        return employeeMapper.selectSubordinates(supervisorId);
    }
}
