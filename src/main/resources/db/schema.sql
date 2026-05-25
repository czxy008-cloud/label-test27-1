-- =============================================================================
-- 员工绩效考核系统 - 数据库初始化脚本
-- 数据库: PostgreSQL
-- 包含: 员工档案、考核指标、评分记录、结果汇总表
-- =============================================================================

-- -----------------------------------------------------------------------------
-- 1. 部门表 - 存储公司组织架构中的部门信息
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS sys_department (
    id              BIGSERIAL       PRIMARY KEY,
    -- 部门名称，如 "技术研发部"、"人力资源部"
    dept_name       VARCHAR(100)    NOT NULL,
    -- 部门编码，唯一标识
    dept_code       VARCHAR(50)     NOT NULL UNIQUE,
    -- 父部门ID，顶级部门为 NULL，支持多级部门树结构
    parent_id       BIGINT          DEFAULT NULL,
    -- 部门负责人ID，关联员工表
    manager_id      BIGINT          DEFAULT NULL,
    -- 部门排序号，数值越小越靠前
    sort_order      INTEGER         DEFAULT 0,
    -- 状态: 1-启用, 0-禁用
    status          SMALLINT        DEFAULT 1,
    -- 创建时间
    create_time     TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    -- 更新时间
    update_time     TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    -- 逻辑删除标志: 1-已删除, 0-未删除
    is_deleted      SMALLINT        DEFAULT 0
);
-- 添加表注释
COMMENT ON TABLE sys_department IS '部门信息表';
COMMENT ON COLUMN sys_department.id IS '部门主键ID';
COMMENT ON COLUMN sys_department.dept_name IS '部门名称';
COMMENT ON COLUMN sys_department.dept_code IS '部门编码';
COMMENT ON COLUMN sys_department.parent_id IS '父部门ID';
COMMENT ON COLUMN sys_department.manager_id IS '部门负责人ID';
COMMENT ON COLUMN sys_department.sort_order IS '排序号';
COMMENT ON COLUMN sys_department.status IS '状态: 1-启用 0-禁用';
COMMENT ON COLUMN sys_department.create_time IS '创建时间';
COMMENT ON COLUMN sys_department.update_time IS '更新时间';
COMMENT ON COLUMN sys_department.is_deleted IS '逻辑删除标志';

-- -----------------------------------------------------------------------------
-- 2. 员工档案表 - 存储员工的基本信息和组织归属
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS emp_employee (
    id              BIGSERIAL       PRIMARY KEY,
    -- 员工工号，全公司唯一
    emp_no          VARCHAR(50)     NOT NULL UNIQUE,
    -- 员工姓名
    emp_name        VARCHAR(100)    NOT NULL,
    -- 登录密码 (BCrypt 加密)
    password        VARCHAR(200)    NOT NULL,
    -- 性别: 1-男, 2-女, 0-未知
    gender          SMALLINT        DEFAULT 0,
    -- 联系电话
    phone           VARCHAR(20),
    -- 电子邮箱
    email           VARCHAR(100),
    -- 所属部门ID，关联 sys_department
    dept_id         BIGINT          NOT NULL,
    -- 职位，如 "高级工程师"、"产品经理"
    position        VARCHAR(100),
    -- 职级，如 P6、M2
    job_level       VARCHAR(20),
    -- 入职日期
    hire_date       DATE,
    -- 员工类型: 1-正式员工, 2-试用期, 3-实习生, 4-外包
    emp_type        SMALLINT        DEFAULT 1,
    -- 直接上级ID，关联 emp_employee，用于考核评分流程
    supervisor_id   BIGINT          DEFAULT NULL,
    -- 角色: admin-管理员, manager-主管, employee-普通员工
    role_code       VARCHAR(20)     DEFAULT 'employee',
    -- 状态: 1-在职, 0-离职
    status          SMALLINT        DEFAULT 1,
    -- 创建时间
    create_time     TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    -- 更新时间
    update_time     TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    -- 逻辑删除标志: 1-已删除, 0-未删除
    is_deleted      SMALLINT        DEFAULT 0
);
COMMENT ON TABLE emp_employee IS '员工档案表';
COMMENT ON COLUMN emp_employee.id IS '员工主键ID';
COMMENT ON COLUMN emp_employee.emp_no IS '员工工号';
COMMENT ON COLUMN emp_employee.emp_name IS '员工姓名';
COMMENT ON COLUMN emp_employee.password IS '登录密码(BCrypt加密)';
COMMENT ON COLUMN emp_employee.gender IS '性别: 1-男 2-女 0-未知';
COMMENT ON COLUMN emp_employee.phone IS '联系电话';
COMMENT ON COLUMN emp_employee.email IS '电子邮箱';
COMMENT ON COLUMN emp_employee.dept_id IS '所属部门ID';
COMMENT ON COLUMN emp_employee.position IS '职位';
COMMENT ON COLUMN emp_employee.job_level IS '职级';
COMMENT ON COLUMN emp_employee.hire_date IS '入职日期';
COMMENT ON COLUMN emp_employee.emp_type IS '员工类型: 1-正式 2-试用期 3-实习 4-外包';
COMMENT ON COLUMN emp_employee.supervisor_id IS '直接上级ID';
COMMENT ON COLUMN emp_employee.role_code IS '角色编码: admin/manager/employee';
COMMENT ON COLUMN emp_employee.status IS '状态: 1-在职 0-离职';
COMMENT ON COLUMN emp_employee.create_time IS '创建时间';
COMMENT ON COLUMN emp_employee.update_time IS '更新时间';
COMMENT ON COLUMN emp_employee.is_deleted IS '逻辑删除标志';

-- 为员工表创建索引，提升查询性能
CREATE INDEX IF NOT EXISTS idx_emp_dept_id ON emp_employee(dept_id);
CREATE INDEX IF NOT EXISTS idx_emp_supervisor_id ON emp_employee(supervisor_id);

-- -----------------------------------------------------------------------------
-- 3. 考核模板表 - 定义绩效考核的模板，包含考核周期和适用范围
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS appraisal_template (
    id                  BIGSERIAL       PRIMARY KEY,
    -- 模板名称，如 "2026年Q2研发绩效考核模板"
    template_name       VARCHAR(200)    NOT NULL,
    -- 考核周期: monthly-月度, quarterly-季度, half_year-半年, yearly-年度
    appraisal_cycle     VARCHAR(20)     NOT NULL,
    -- 考核周期的具体值，如季度考核为 2026-Q2
    cycle_value         VARCHAR(20),
    -- 适用部门ID，NULL 表示全公司适用
    applicable_dept_id  BIGINT          DEFAULT NULL,
    -- 自评权重 (0-100)，上级评分为 100-自评权重
    self_weight         DECIMAL(5,2)    DEFAULT 40.00,
    -- 自评开始时间
    self_start_time     TIMESTAMP,
    -- 自评截止时间
    self_end_time       TIMESTAMP,
    -- 上级评分开始时间
    supervisor_start_time   TIMESTAMP,
    -- 上级评分截止时间
    supervisor_end_time     TIMESTAMP,
    -- 结果公示开始时间
    publish_start_time      TIMESTAMP,
    -- 结果公示结束时间
    publish_end_time        TIMESTAMP,
    -- 模板状态: draft-草稿, active-进行中, completed-已完成, closed-已关闭
    template_status     VARCHAR(20)     DEFAULT 'draft',
    -- 模板描述
    description         TEXT,
    -- 创建人ID
    created_by          BIGINT,
    -- 创建时间
    create_time         TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    -- 更新时间
    update_time         TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    -- 逻辑删除标志
    is_deleted          SMALLINT        DEFAULT 0
);
COMMENT ON TABLE appraisal_template IS '考核模板表';
COMMENT ON COLUMN appraisal_template.id IS '模板主键ID';
COMMENT ON COLUMN appraisal_template.template_name IS '模板名称';
COMMENT ON COLUMN appraisal_template.appraisal_cycle IS '考核周期: monthly/quarterly/half_year/yearly';
COMMENT ON COLUMN appraisal_template.cycle_value IS '周期具体值';
COMMENT ON COLUMN appraisal_template.applicable_dept_id IS '适用部门ID';
COMMENT ON COLUMN appraisal_template.self_weight IS '自评权重';
COMMENT ON COLUMN appraisal_template.self_start_time IS '自评开始时间';
COMMENT ON COLUMN appraisal_template.self_end_time IS '自评截止时间';
COMMENT ON COLUMN appraisal_template.supervisor_start_time IS '上级评分开始时间';
COMMENT ON COLUMN appraisal_template.supervisor_end_time IS '上级评分截止时间';
COMMENT ON COLUMN appraisal_template.publish_start_time IS '结果公示开始时间';
COMMENT ON COLUMN appraisal_template.publish_end_time IS '结果公示结束时间';
COMMENT ON COLUMN appraisal_template.template_status IS '模板状态: draft/active/completed/closed';
COMMENT ON COLUMN appraisal_template.description IS '模板描述';
COMMENT ON COLUMN appraisal_template.created_by IS '创建人ID';
COMMENT ON COLUMN appraisal_template.create_time IS '创建时间';
COMMENT ON COLUMN appraisal_template.update_time IS '更新时间';
COMMENT ON COLUMN appraisal_template.is_deleted IS '逻辑删除标志';

-- -----------------------------------------------------------------------------
-- 4. 考核指标表 - 考核模板下的具体考核指标，支持多级指标和权重配置
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS appraisal_indicator (
    id              BIGSERIAL       PRIMARY KEY,
    -- 所属模板ID，关联 appraisal_template
    template_id     BIGINT          NOT NULL,
    -- 父指标ID，顶级指标为 NULL，支持二级指标结构
    parent_id       BIGINT          DEFAULT NULL,
    -- 指标名称，如 "工作业绩"、"技术能力"、"团队协作"
    indicator_name  VARCHAR(200)    NOT NULL,
    -- 指标编码，用于唯一标识
    indicator_code  VARCHAR(50),
    -- 指标类型: quantitative-定量(有具体分值), qualitative-定性(描述性评价)
    indicator_type  VARCHAR(20)     DEFAULT 'quantitative',
    -- 指标权重 (0-100)，同一模板下所有一级指标权重之和应为 100
    weight          DECIMAL(5,2)    NOT NULL DEFAULT 0.00,
    -- 指标满分值，一般为 100
    max_score       DECIMAL(8,2)    DEFAULT 100.00,
    -- 评分标准描述，如 "90-100: 卓越表现; 80-89: 超出预期"
    scoring_criteria    TEXT,
    -- 指标描述
    description     TEXT,
    -- 指标排序号
    sort_order      INTEGER         DEFAULT 0,
    -- 状态: 1-启用, 0-禁用
    status          SMALLINT        DEFAULT 1,
    -- 创建时间
    create_time     TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    -- 更新时间
    update_time     TIMESTAMP       DEFAULT CURRENT_TIMESTAMP
);
COMMENT ON TABLE appraisal_indicator IS '考核指标表';
COMMENT ON COLUMN appraisal_indicator.id IS '指标主键ID';
COMMENT ON COLUMN appraisal_indicator.template_id IS '所属模板ID';
COMMENT ON COLUMN appraisal_indicator.parent_id IS '父指标ID';
COMMENT ON COLUMN appraisal_indicator.indicator_name IS '指标名称';
COMMENT ON COLUMN appraisal_indicator.indicator_code IS '指标编码';
COMMENT ON COLUMN appraisal_indicator.indicator_type IS '指标类型: quantitative-定量 qualitative-定性';
COMMENT ON COLUMN appraisal_indicator.weight IS '指标权重';
COMMENT ON COLUMN appraisal_indicator.max_score IS '指标满分值';
COMMENT ON COLUMN appraisal_indicator.scoring_criteria IS '评分标准描述';
COMMENT ON COLUMN appraisal_indicator.description IS '指标描述';
COMMENT ON COLUMN appraisal_indicator.sort_order IS '排序号';
COMMENT ON COLUMN appraisal_indicator.status IS '状态: 1-启用 0-禁用';
COMMENT ON COLUMN appraisal_indicator.create_time IS '创建时间';
COMMENT ON COLUMN appraisal_indicator.update_time IS '更新时间';

CREATE INDEX IF NOT EXISTS idx_indicator_template_id ON appraisal_indicator(template_id);
CREATE INDEX IF NOT EXISTS idx_indicator_parent_id ON appraisal_indicator(parent_id);

-- -----------------------------------------------------------------------------
-- 5. 评分记录表 - 存储每个员工在具体指标上的自评和上级评分
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS appraisal_score_record (
    id              BIGSERIAL       PRIMARY KEY,
    -- 所属模板ID
    template_id     BIGINT          NOT NULL,
    -- 被考核员工ID
    employee_id     BIGINT          NOT NULL,
    -- 考核指标ID，关联 appraisal_indicator
    indicator_id    BIGINT          NOT NULL,
    -- 自评得分
    self_score      DECIMAL(8,2),
    -- 自评语
    self_comment    TEXT,
    -- 自评提交时间
    self_submit_time    TIMESTAMP,
    -- 上级评分人ID
    supervisor_id   BIGINT,
    -- 上级评分
    supervisor_score    DECIMAL(8,2),
    -- 上级评语
    supervisor_comment  TEXT,
    -- 上级评分提交时间
    supervisor_submit_time  TIMESTAMP,
    -- 综合得分 (根据权重计算)
    final_score     DECIMAL(8,2),
    -- 状态: pending-待评分, self_done-自评完成, supervisor_done-上级评分完成, confirmed-已确认
    score_status    VARCHAR(20)     DEFAULT 'pending',
    -- 创建时间
    create_time     TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    -- 更新时间
    update_time     TIMESTAMP       DEFAULT CURRENT_TIMESTAMP
);
COMMENT ON TABLE appraisal_score_record IS '考核评分记录表';
COMMENT ON COLUMN appraisal_score_record.id IS '评分记录主键ID';
COMMENT ON COLUMN appraisal_score_record.template_id IS '所属模板ID';
COMMENT ON COLUMN appraisal_score_record.employee_id IS '被考核员工ID';
COMMENT ON COLUMN appraisal_score_record.indicator_id IS '考核指标ID';
COMMENT ON COLUMN appraisal_score_record.self_score IS '自评得分';
COMMENT ON COLUMN appraisal_score_record.self_comment IS '自评语';
COMMENT ON COLUMN appraisal_score_record.self_submit_time IS '自评提交时间';
COMMENT ON COLUMN appraisal_score_record.supervisor_id IS '上级评分人ID';
COMMENT ON COLUMN appraisal_score_record.supervisor_score IS '上级评分';
COMMENT ON COLUMN appraisal_score_record.supervisor_comment IS '上级评语';
COMMENT ON COLUMN appraisal_score_record.supervisor_submit_time IS '上级评分提交时间';
COMMENT ON COLUMN appraisal_score_record.final_score IS '综合得分';
COMMENT ON COLUMN appraisal_score_record.score_status IS '评分状态';
COMMENT ON COLUMN appraisal_score_record.create_time IS '创建时间';
COMMENT ON COLUMN appraisal_score_record.update_time IS '更新时间';

CREATE INDEX IF NOT EXISTS idx_score_template_emp ON appraisal_score_record(template_id, employee_id);
CREATE INDEX IF NOT EXISTS idx_score_indicator ON appraisal_score_record(indicator_id);

-- -----------------------------------------------------------------------------
-- 6. 考核结果汇总表 - 存储每个员工在一个考核周期内的最终考核结果
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS appraisal_result (
    id              BIGSERIAL       PRIMARY KEY,
    -- 所属模板ID
    template_id     BIGINT          NOT NULL,
    -- 被考核员工ID
    employee_id     BIGINT          NOT NULL,
    -- 员工所属部门ID（冗余字段，方便公示查询）
    dept_id         BIGINT,
    -- 自评加权总分
    self_total_score    DECIMAL(8,2),
    -- 上级评分加权总分
    supervisor_total_score  DECIMAL(8,2),
    -- 最终综合得分 (自评 * 自评权重 + 上级评分 * 上级权重)
    final_total_score   DECIMAL(8,2),
    -- 考核等级: S-卓越, A-优秀, B-良好, C-合格, D-不合格
    appraisal_grade     VARCHAR(10),
    -- 等级说明
    grade_comment       TEXT,
    -- 综合评语
    overall_comment     TEXT,
    -- 结果状态: draft-草稿, confirmed-已确认, published-已公示
    result_status       VARCHAR(20)     DEFAULT 'draft',
    -- 员工确认状态: 0-未确认, 1-已确认, 2-已申诉
    confirm_status      SMALLINT        DEFAULT 0,
    -- 确认时间
    confirm_time        TIMESTAMP,
    -- 公示开始时间
    publish_start_time  TIMESTAMP,
    -- 创建时间
    create_time         TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    -- 更新时间
    update_time         TIMESTAMP       DEFAULT CURRENT_TIMESTAMP
);
COMMENT ON TABLE appraisal_result IS '考核结果汇总表';
COMMENT ON COLUMN appraisal_result.id IS '结果主键ID';
COMMENT ON COLUMN appraisal_result.template_id IS '所属模板ID';
COMMENT ON COLUMN appraisal_result.employee_id IS '被考核员工ID';
COMMENT ON COLUMN appraisal_result.dept_id IS '员工所属部门ID';
COMMENT ON COLUMN appraisal_result.self_total_score IS '自评加权总分';
COMMENT ON COLUMN appraisal_result.supervisor_total_score IS '上级评分加权总分';
COMMENT ON COLUMN appraisal_result.final_total_score IS '最终综合得分';
COMMENT ON COLUMN appraisal_result.appraisal_grade IS '考核等级: S/A/B/C/D';
COMMENT ON COLUMN appraisal_result.grade_comment IS '等级说明';
COMMENT ON COLUMN appraisal_result.overall_comment IS '综合评语';
COMMENT ON COLUMN appraisal_result.result_status IS '结果状态: draft/confirmed/published';
COMMENT ON COLUMN appraisal_result.confirm_status IS '员工确认状态: 0-未确认 1-已确认 2-已申诉';
COMMENT ON COLUMN appraisal_result.confirm_time IS '确认时间';
COMMENT ON COLUMN appraisal_result.publish_start_time IS '公示开始时间';
COMMENT ON COLUMN appraisal_result.create_time IS '创建时间';
COMMENT ON COLUMN appraisal_result.update_time IS '更新时间';

-- 确保每个员工在同一模板下只有一条结果记录
CREATE UNIQUE INDEX IF NOT EXISTS uk_result_template_emp ON appraisal_result(template_id, employee_id);

-- -----------------------------------------------------------------------------
-- 7. 申诉记录表 - 员工对考核结果的申诉
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS appraisal_appeal (
    id              BIGSERIAL       PRIMARY KEY,
    -- 关联考核结果ID
    result_id       BIGINT          NOT NULL,
    -- 申诉人ID
    appellant_id    BIGINT          NOT NULL,
    -- 申诉原因
    appeal_reason   TEXT            NOT NULL,
    -- 申诉状态: pending-处理中, approved-已同意, rejected-已驳回
    appeal_status   VARCHAR(20)     DEFAULT 'pending',
    -- 处理人ID
    handler_id      BIGINT,
    -- 处理意见
    handle_comment  TEXT,
    -- 处理时间
    handle_time     TIMESTAMP,
    -- 创建时间
    create_time     TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    -- 更新时间
    update_time     TIMESTAMP       DEFAULT CURRENT_TIMESTAMP
);
COMMENT ON TABLE appraisal_appeal IS '考核申诉记录表';
COMMENT ON COLUMN appraisal_appeal.id IS '申诉主键ID';
COMMENT ON COLUMN appraisal_appeal.result_id IS '考核结果ID';
COMMENT ON COLUMN appraisal_appeal.appellant_id IS '申诉人ID';
COMMENT ON COLUMN appraisal_appeal.appeal_reason IS '申诉原因';
COMMENT ON COLUMN appraisal_appeal.appeal_status IS '申诉状态: pending/approved/rejected';
COMMENT ON COLUMN appraisal_appeal.handler_id IS '处理人ID';
COMMENT ON COLUMN appraisal_appeal.handle_comment IS '处理意见';
COMMENT ON COLUMN appraisal_appeal.handle_time IS '处理时间';
COMMENT ON COLUMN appraisal_appeal.create_time IS '创建时间';
COMMENT ON COLUMN appraisal_appeal.update_time IS '更新时间';

-- -----------------------------------------------------------------------------
-- 初始化数据
-- -----------------------------------------------------------------------------

-- 初始化部门数据
INSERT INTO sys_department (dept_name, dept_code, parent_id, sort_order) VALUES
    ('总经办', 'D001', NULL, 1),
    ('技术研发部', 'D002', NULL, 2),
    ('人力资源部', 'D003', NULL, 3),
    ('财务部', 'D004', NULL, 4),
    ('前端开发组', 'D002-01', 2, 1),
    ('后端开发组', 'D002-02', 2, 2);

-- 初始化管理员账号 (密码: admin123，BCrypt加密)
INSERT INTO emp_employee (emp_no, emp_name, password, gender, dept_id, position, job_level, hire_date, emp_type, role_code) VALUES
    ('E000', '系统管理员', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EHsM8lE9', 1, 1, '系统管理员', 'M1', '2020-01-01', 1, 'admin');

-- 初始化测试员工数据 (密码: 123456，BCrypt加密)
INSERT INTO emp_employee (emp_no, emp_name, password, gender, phone, email, dept_id, position, job_level, hire_date, emp_type, supervisor_id, role_code) VALUES
    ('E001', '张三', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EHsM8lE9', 1, '13800138001', 'zhangsan@example.com', 5, '高级前端工程师', 'P6', '2021-03-15', 1, 3, 'employee'),
    ('E002', '李四', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EHsM8lE9', 1, '13800138002', 'lisi@example.com', 6, '后端工程师', 'P5', '2022-06-01', 1, 3, 'employee'),
    ('E003', '王五', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EHsM8lE9', 2, '13800138003', 'wangwu@example.com', 2, '研发经理', 'M2', '2019-01-10', 1, 1, 'manager'),
    ('E004', '赵六', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EHsM8lE9', 2, '13800138004', 'zhaoliu@example.com', 3, 'HR专员', 'P4', '2023-02-20', 1, NULL, 'employee');

-- 初始化一个考核模板
INSERT INTO appraisal_template (template_name, appraisal_cycle, cycle_value, self_weight, self_start_time, self_end_time, supervisor_start_time, supervisor_end_time, publish_start_time, publish_end_time, template_status, description, created_by) VALUES
    ('2026年Q2研发绩效考核模板', 'quarterly', '2026-Q2', 40.00, '2026-07-01 00:00:00', '2026-07-10 23:59:59', '2026-07-11 00:00:00', '2026-07-15 23:59:59', '2026-07-16 00:00:00', '2026-07-31 23:59:59', 'active', '2026年第二季度研发部门绩效考核模板，包含工作业绩、技术能力、团队协作等指标', 1);

-- 初始化考核指标 (一级指标权重之和为100)
INSERT INTO appraisal_indicator (template_id, parent_id, indicator_name, indicator_code, indicator_type, weight, max_score, scoring_criteria, sort_order) VALUES
    (1, NULL, '工作业绩', 'KPI001', 'quantitative', 50.00, 100.00, '90-100: 超额完成目标; 80-89: 超出预期; 70-79: 符合预期; 60-69: 待改进; 0-59: 未达标', 1),
    (1, NULL, '技术能力', 'KPI002', 'quantitative', 25.00, 100.00, '90-100: 技术精通; 80-89: 技术扎实; 70-79: 满足需求; 60-69: 需要提升; 0-59: 技术不足', 2),
    (1, NULL, '团队协作', 'KPI003', 'quantitative', 15.00, 100.00, '90-100: 积极协作; 80-89: 主动配合; 70-79: 基本配合; 60-69: 协作不够; 0-59: 协作困难', 3),
    (1, NULL, '工作态度', 'KPI004', 'quantitative', 10.00, 100.00, '90-100: 工作积极; 80-89: 态度端正; 70-79: 基本称职; 60-69: 有所懈怠; 0-59: 态度消极', 4);

-- 初始化二级指标示例
INSERT INTO appraisal_indicator (template_id, parent_id, indicator_name, indicator_code, indicator_type, weight, max_score, scoring_criteria, sort_order) VALUES
    (1, 1, '任务完成率', 'KPI001-01', 'quantitative', 40.00, 100.00, '90-100: 100%以上; 80-89: 90-100%; 70-79: 80-90%; 60-69: 70-80%; 0-59: 70%以下', 1),
    (1, 1, '任务质量', 'KPI001-02', 'quantitative', 60.00, 100.00, '90-100: 零缺陷; 80-89: 少量bug; 70-79: 基本合格; 60-69: 较多缺陷; 0-59: 严重质量问题', 2);
