package com.sl.mentalhealth.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sl.mentalhealth.entity.AssessmentScale;
import com.sl.mentalhealth.entity.AssessmentScaleVersion;
import com.sl.mentalhealth.entity.AssessmentVersionOption;
import com.sl.mentalhealth.entity.AssessmentVersionQuestion;
import com.sl.mentalhealth.mapper.AssessmentScaleMapper;
import com.sl.mentalhealth.mapper.AssessmentScaleVersionMapper;
import com.sl.mentalhealth.mapper.AssessmentVersionOptionMapper;
import com.sl.mentalhealth.mapper.AssessmentVersionQuestionMapper;
import com.sl.mentalhealth.vo.AssessmentOptionVO;
import com.sl.mentalhealth.vo.AssessmentQuestionVO;
import com.sl.mentalhealth.vo.AssessmentScaleDetailVO;
import com.sl.mentalhealth.vo.AssessmentScaleVO;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AssessmentScaleCacheService {

  private final AssessmentScaleMapper scaleMapper;
  private final AssessmentScaleVersionMapper scaleVersionMapper;
  private final AssessmentVersionQuestionMapper versionQuestionMapper;
  private final AssessmentVersionOptionMapper versionOptionMapper;

  @Cacheable(cacheNames = "assessmentScale", key = "'list'")
  public List<AssessmentScaleVO> listScales() {
    return scaleMapper.selectList(
            new LambdaQueryWrapper<AssessmentScale>()
                .eq(AssessmentScale::getStatus, 1)
                .orderByAsc(AssessmentScale::getId)
        ).stream()
        .filter(scale -> !Objects.equals(scale.getDeletedFlag(), 1))
        .filter(scale -> scale.getCurrentVersionId() != null)
        .map(this::buildScaleVO)
        .collect(Collectors.toList());
  }

  @Cacheable(cacheNames = "assessmentScaleDetail", key = "#scaleId", unless = "#result == null")
  public AssessmentScaleDetailVO getDetail(Long scaleId) {
    AssessmentScale scale = scaleMapper.selectById(scaleId);
    if (scale == null || Objects.equals(scale.getDeletedFlag(), 1)
        || !Objects.equals(scale.getStatus(), 1)) {
      throw new RuntimeException("量表不存在或已停用");
    }

    Long versionId = scale.getCurrentVersionId();
    if (versionId == null) {
      throw new RuntimeException("当前量表没有可用版本");
    }

    AssessmentScaleVersion version = scaleVersionMapper.selectById(versionId);
    if (version == null) {
      throw new RuntimeException("量表当前版本不存在");
    }

    List<AssessmentVersionQuestion> questions = versionQuestionMapper.selectList(
        new LambdaQueryWrapper<AssessmentVersionQuestion>()
            .eq(AssessmentVersionQuestion::getVersionId, versionId)
            .orderByAsc(AssessmentVersionQuestion::getQuestionNo)
    );

    if (questions.isEmpty()) {
      throw new RuntimeException("量表题目不存在");
    }

    List<Long> questionIds = questions.stream()
        .map(AssessmentVersionQuestion::getId)
        .toList();

    List<AssessmentVersionOption> options = questionIds.isEmpty()
        ? Collections.emptyList()
        : versionOptionMapper.selectList(
            new LambdaQueryWrapper<AssessmentVersionOption>()
                .in(AssessmentVersionOption::getVersionQuestionId, questionIds)
                .orderByAsc(AssessmentVersionOption::getVersionQuestionId)
                .orderByAsc(AssessmentVersionOption::getOptionNo)
        );

    Map<Long, List<AssessmentOptionVO>> optionMap = options.stream()
        .collect(Collectors.groupingBy(
            AssessmentVersionOption::getVersionQuestionId,
            LinkedHashMap::new,
            Collectors.mapping(
                option -> new AssessmentOptionVO(
                    option.getId(),
                    option.getOptionNo(),
                    option.getOptionText(),
                    option.getOptionScore()
                ),
                Collectors.toList()
            )
        ));

    List<AssessmentQuestionVO> questionVOList = questions.stream()
        .map(question -> new AssessmentQuestionVO(
            question.getId(),
            question.getQuestionNo(),
            question.getQuestionText(),
            question.getRequiredFlag(),
            optionMap.getOrDefault(question.getId(), new ArrayList<>())
        ))
        .collect(Collectors.toList());

    return new AssessmentScaleDetailVO(
        scale.getId(),
        scale.getScaleCode(),
        scale.getScaleName(),
        scale.getDescription(),
        version.getId(),
        version.getVersionNo(),
        questionVOList
    );
  }

  @Caching(evict = {
      @CacheEvict(cacheNames = "assessmentScale", allEntries = true),
      @CacheEvict(cacheNames = "assessmentScaleDetail", allEntries = true)
  })
  public void evictAllAssessmentScaleCaches() {
    // 管理员新增、修改、删除、发布量表后调用
  }

  private AssessmentScaleVO buildScaleVO(AssessmentScale scale) {
    Integer versionNo = null;
    if (scale.getCurrentVersionId() != null) {
      AssessmentScaleVersion version = scaleVersionMapper.selectById(scale.getCurrentVersionId());
      versionNo = version == null ? null : version.getVersionNo();
    }

    return new AssessmentScaleVO(
        scale.getId(),
        scale.getScaleCode(),
        scale.getScaleName(),
        scale.getScaleType(),
        scale.getDescription(),
        scale.getQuestionCount(),
        scale.getCurrentVersionId(),
        versionNo
    );
  }
}