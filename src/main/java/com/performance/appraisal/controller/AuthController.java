package com.performance.appraisal.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import com.performance.appraisal.common.Result;
import com.performance.appraisal.dto.LoginDTO;
import com.performance.appraisal.entity.EmpEmployee;
import com.performance.appraisal.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @SaIgnore
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Valid @RequestBody LoginDTO loginDTO) {
        String token = authService.login(loginDTO);
        EmpEmployee user = authService.getCurrentUser();

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("userId", user.getId());
        data.put("empName", user.getEmpName());
        data.put("empNo", user.getEmpNo());
        data.put("roleCode", user.getRoleCode());

        return Result.success("登录成功", data);
    }

    @PostMapping("/logout")
    public Result<Void> logout() {
        authService.logout();
        return Result.success();
    }

    @GetMapping("/userinfo")
    public Result<Map<String, Object>> userinfo() {
        EmpEmployee user = authService.getCurrentUser();

        Map<String, Object> data = new HashMap<>();
        data.put("userId", user.getId());
        data.put("empName", user.getEmpName());
        data.put("empNo", user.getEmpNo());
        data.put("roleCode", user.getRoleCode());
        data.put("deptId", user.getDeptId());
        data.put("position", user.getPosition());
        data.put("email", user.getEmail());
        data.put("phone", user.getPhone());

        return Result.success(data);
    }
}
