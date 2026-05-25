package com.performance.appraisal.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AppraisalAppeal {

    private Long id;

    private Long resultId;

    private Long appellantId;

    private String appealReason;

    private String appealStatus;

    private Long handlerId;

    private String handleComment;

    private LocalDateTime handleTime;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
