package com.performance.appraisal.mapper;

import com.performance.appraisal.entity.AppraisalTemplate;
import com.performance.appraisal.dto.TemplateDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TemplateMapper {

    AppraisalTemplate selectById(@Param("id") Long id);

    List<TemplateDTO> selectList(@Param("templateName") String templateName,
                                  @Param("templateStatus") String templateStatus,
                                  @Param("appraisalCycle") String appraisalCycle);

    int insert(AppraisalTemplate template);

    int update(AppraisalTemplate template);

    int deleteById(@Param("id") Long id);

    List<AppraisalTemplate> selectActiveTemplates();
}
