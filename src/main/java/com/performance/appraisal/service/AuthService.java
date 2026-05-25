package com.performance.appraisal.service;

import com.performance.appraisal.dto.LoginDTO;
import com.performance.appraisal.entity.EmpEmployee;

public interface AuthService {

    String login(LoginDTO loginDTO);

    void logout();

    EmpEmployee getCurrentUser();
}
