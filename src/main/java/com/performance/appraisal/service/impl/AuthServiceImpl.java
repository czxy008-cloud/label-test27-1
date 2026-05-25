package com.performance.appraisal.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.performance.appraisal.common.ResultCode;
import com.performance.appraisal.dto.LoginDTO;
import com.performance.appraisal.entity.EmpEmployee;
import com.performance.appraisal.mapper.EmployeeMapper;
import com.performance.appraisal.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private EmployeeMapper employeeMapper;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public String login(LoginDTO loginDTO) {
        EmpEmployee employee = employeeMapper.selectByEmpNo(loginDTO.getEmpNo());
        if (employee == null) {
            throw new RuntimeException(ResultCode.LOGIN_ERROR.getMessage());
        }
        if (!passwordEncoder.matches(loginDTO.getPassword(), employee.getPassword())) {
            throw new RuntimeException(ResultCode.LOGIN_ERROR.getMessage());
        }
        if (employee.getStatus() != null && employee.getStatus() == 0) {
            throw new RuntimeException(ResultCode.EMPLOYEE_DISABLED.getMessage());
        }
        StpUtil.login(employee.getId());
        return StpUtil.getTokenValue();
    }

    @Override
    public void logout() {
        StpUtil.logout();
    }

    @Override
    public EmpEmployee getCurrentUser() {
        long userId = StpUtil.getLoginIdAsLong();
        return employeeMapper.selectById(userId);
    }
}
