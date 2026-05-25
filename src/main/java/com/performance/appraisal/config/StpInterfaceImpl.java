package com.performance.appraisal.config;

import cn.dev33.satoken.stp.StpInterface;
import com.performance.appraisal.entity.EmpEmployee;
import com.performance.appraisal.mapper.EmployeeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class StpInterfaceImpl implements StpInterface {

    @Autowired
    private EmployeeMapper employeeMapper;

    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        return new ArrayList<>();
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        Long id = Long.valueOf(loginId.toString());
        EmpEmployee employee = employeeMapper.selectById(id);
        if (employee != null && employee.getRoleCode() != null) {
            return Collections.singletonList(employee.getRoleCode());
        }
        return new ArrayList<>();
    }
}
