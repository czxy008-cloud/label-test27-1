package com.performance.appraisal.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginDTO {

    @NotBlank(message = "员工工号不能为空")
    private String empNo;

    @NotBlank(message = "密码不能为空")
    private String password;
}
