package com.performance.appraisal.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResultCode {

    SUCCESS(200, "操作成功"),
    PARAM_ERROR(400, "参数错误"),
    UNAUTHORIZED(401, "未登录或登录已过期"),
    FORBIDDEN(403, "权限不足"),
    NOT_FOUND(404, "资源不存在"),
    BUSINESS_ERROR(500, "业务处理失败"),
    SYSTEM_ERROR(501, "系统内部错误"),
    LOGIN_ERROR(502, "用户名或密码错误"),
    TEMPLATE_NOT_FOUND(601, "考核模板不存在"),
    TEMPLATE_NOT_ACTIVE(602, "考核模板未启用"),
    SCORE_ALREADY_EXISTS(603, "评分记录已存在"),
    SCORE_NOT_FOUND(604, "评分记录不存在"),
    RESULT_NOT_FOUND(605, "考核结果不存在"),
    NOT_IN_SELF_PERIOD(606, "不在自评时间范围内"),
    NOT_IN_SUPERVISOR_PERIOD(607, "不在上级评分时间范围内"),
    ALREADY_CONFIRMED(608, "考核结果已确认"),
    DEPT_NOT_FOUND(701, "部门不存在"),
    EMPLOYEE_NOT_FOUND(702, "员工不存在"),
    EMPLOYEE_DISABLED(703, "员工已离职"),
    INDICATOR_WEIGHT_ERROR(704, "指标权重配置错误"),
    DUPLICATE_EMP_NO(705, "员工工号已存在");

    private final Integer code;
    private final String message;
}
