package com.performance.appraisal.mapper;

import com.performance.appraisal.entity.AppraisalIndicator;
import com.performance.appraisal.dto.IndicatorDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface IndicatorMapper {

    AppraisalIndicator selectById(@Param("id") Long id);

    List<AppraisalIndicator> selectByTemplateId(@Param("templateId") Long templateId);

    List<IndicatorDTO> selectTreeByTemplateId(@Param("templateId") Long templateId);

    List<AppraisalIndicator> selectByParentId(@Param("parentId") Long parentId);

    int insert(AppraisalIndicator indicator);

    int update(AppraisalIndicator indicator);

    int deleteById(@Param("id") Long id);

    int deleteByTemplateId(@Param("templateId") Long templateId);
}
