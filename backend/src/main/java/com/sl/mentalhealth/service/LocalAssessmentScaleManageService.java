package com.sl.mentalhealth.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sl.mentalhealth.dto.AssessmentScaleUpdateRequest;
import com.sl.mentalhealth.entity.AssessmentScale;
import com.sl.mentalhealth.entity.AssessmentScaleVersion;
import com.sl.mentalhealth.entity.AssessmentVersionOption;
import com.sl.mentalhealth.entity.AssessmentVersionQuestion;
import com.sl.mentalhealth.entity.AssessmentVersionRule;
import com.sl.mentalhealth.kafka.message.AssessmentScaleManageRequestMessage;
import com.sl.mentalhealth.mapper.AssessmentScaleMapper;
import com.sl.mentalhealth.mapper.AssessmentScaleVersionMapper;
import com.sl.mentalhealth.mapper.AssessmentVersionOptionMapper;
import com.sl.mentalhealth.mapper.AssessmentVersionQuestionMapper;
import com.sl.mentalhealth.mapper.AssessmentVersionRuleMapper;
import com.sl.mentalhealth.vo.AssessmentScaleManageVO;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LocalAssessmentScaleManageService {

  private final AssessmentScaleMapper assessmentScaleMapper;
  private final AssessmentScaleVersionMapper assessmentScaleVersionMapper;
  private final AssessmentVersionQuestionMapper assessmentVersionQuestionMapper;
  private final AssessmentVersionOptionMapper assessmentVersionOptionMapper;
  private final AssessmentVersionRuleMapper assessmentVersionRuleMapper;

  public LocalAssessmentScaleManageService(
      AssessmentScaleMapper assessmentScaleMapper,
      AssessmentScaleVersionMapper assessmentScaleVersionMapper,
      AssessmentVersionQuestionMapper assessmentVersionQuestionMapper,
      AssessmentVersionOptionMapper assessmentVersionOptionMapper,
      AssessmentVersionRuleMapper assessmentVersionRuleMapper) {
    this.assessmentScaleMapper = assessmentScaleMapper;
    this.assessmentScaleVersionMapper = assessmentScaleVersionMapper;
    this.assessmentVersionQuestionMapper = assessmentVersionQuestionMapper;
    this.assessmentVersionOptionMapper = assessmentVersionOptionMapper;
    this.assessmentVersionRuleMapper = assessmentVersionRuleMapper;
  }

  @Transactional
  public String importScale(AssessmentScaleManageRequestMessage request) {
    AssessmentScale exists = assessmentScaleMapper.selectOne(
        new LambdaQueryWrapper<AssessmentScale>()
            .eq(AssessmentScale::getScaleCode, request.getScaleCode())
    );
    if (exists != null) {
      throw new RuntimeException("量表编码已存在");
    }
    if (request.getQuestions() == null || request.getQuestions().isEmpty()) {
      throw new RuntimeException("题目不能为空");
    }
    if (request.getRules() == null || request.getRules().isEmpty()) {
      throw new RuntimeException("规则不能为空");
    }

    int scoreMin = request.getRules().stream()
        .mapToInt(AssessmentScaleUpdateRequest.RuleDTO::getMinScore)
        .min()
        .orElse(0);
    int scoreMax = request.getRules().stream()
        .mapToInt(AssessmentScaleUpdateRequest.RuleDTO::getMaxScore)
        .max()
        .orElse(0);

    AssessmentScale scale = new AssessmentScale();
    scale.setScaleCode(request.getScaleCode());
    scale.setScaleName(request.getScaleName());
    scale.setScaleType(request.getScaleType());
    scale.setDescription(request.getDescription());
    scale.setQuestionCount(request.getQuestions().size());
    scale.setScoreMin(scoreMin);
    scale.setScoreMax(scoreMax);
    scale.setStatus(1);
    scale.setDeletedFlag(0);
    scale.setCreatedBy(request.getOperator());
    assessmentScaleMapper.insert(scale);

    AssessmentScaleVersion version = new AssessmentScaleVersion();
    version.setScaleId(scale.getId());
    version.setVersionNo(1);
    version.setVersionStatus("ACTIVE");
    version.setSourceQuestionFileName(request.getQuestionFileName());
    version.setSourceRuleFileName(request.getRuleFileName());
    version.setVersionRemark("首次导入");
    version.setCreatedBy(request.getOperator());
    assessmentScaleVersionMapper.insert(version);

    saveQuestionsAndRules(version.getId(), request.getQuestions(), request.getRules());

    scale.setCurrentVersionId(version.getId());
    assessmentScaleMapper.updateById(scale);

    return "导入成功";
  }

  public List<AssessmentScaleManageVO> listAll() {
    List<AssessmentScale> scales = assessmentScaleMapper.selectList(
        new LambdaQueryWrapper<AssessmentScale>()
            .eq(AssessmentScale::getDeletedFlag, 0)
            .orderByDesc(AssessmentScale::getId)
    );

    Map<Long, AssessmentScaleVersion> versionMap = assessmentScaleVersionMapper.selectList(null).stream()
        .collect(Collectors.toMap(AssessmentScaleVersion::getId, v -> v, (a, b) -> a));

    List<AssessmentScaleManageVO> result = new ArrayList<>();
    for (AssessmentScale scale : scales) {
      AssessmentScaleManageVO vo = new AssessmentScaleManageVO();
      vo.setScaleId(scale.getId());
      vo.setScaleCode(scale.getScaleCode());
      vo.setScaleName(scale.getScaleName());
      vo.setScaleType(scale.getScaleType());
      vo.setQuestionCount(scale.getQuestionCount());
      vo.setScoreMin(scale.getScoreMin());
      vo.setScoreMax(scale.getScoreMax());
      vo.setStatus(scale.getStatus());
      vo.setDeletedFlag(scale.getDeletedFlag());
      if (scale.getCurrentVersionId() != null && versionMap.containsKey(scale.getCurrentVersionId())) {
        vo.setCurrentVersionNo(versionMap.get(scale.getCurrentVersionId()).getVersionNo());
      }
      result.add(vo);
    }
    return result;
  }

  public Map<String, Object> getScaleDetail(Long scaleId) {
    AssessmentScale scale = assessmentScaleMapper.selectById(scaleId);
    if (scale == null) {
      throw new RuntimeException("量表不存在");
    }

    if (scale.getCurrentVersionId() == null) {
      throw new RuntimeException("量表当前版本不存在");
    }

    AssessmentScaleVersion version = assessmentScaleVersionMapper.selectById(scale.getCurrentVersionId());
    if (version == null) {
      throw new RuntimeException("版本不存在");
    }

    List<AssessmentVersionQuestion> questions = assessmentVersionQuestionMapper.selectList(
        new LambdaQueryWrapper<AssessmentVersionQuestion>()
            .eq(AssessmentVersionQuestion::getVersionId, version.getId())
            .orderByAsc(AssessmentVersionQuestion::getQuestionNo)
    );

    List<Long> questionIds = questions.stream().map(AssessmentVersionQuestion::getId).toList();
    List<AssessmentVersionOption> options = questionIds.isEmpty()
        ? Collections.emptyList()
        : assessmentVersionOptionMapper.selectList(
            new LambdaQueryWrapper<AssessmentVersionOption>()
                .in(AssessmentVersionOption::getVersionQuestionId, questionIds)
                .orderByAsc(AssessmentVersionOption::getVersionQuestionId)
                .orderByAsc(AssessmentVersionOption::getOptionNo)
        );

    Map<Long, List<AssessmentVersionOption>> optionMap = options.stream()
        .collect(Collectors.groupingBy(AssessmentVersionOption::getVersionQuestionId));

    List<Map<String, Object>> questionList = new ArrayList<>();
    for (AssessmentVersionQuestion question : questions) {
      Map<String, Object> item = new LinkedHashMap<>();
      item.put("id", question.getId());
      item.put("questionNo", question.getQuestionNo());
      item.put("questionText", question.getQuestionText());
      item.put(
          "options",
          optionMap.getOrDefault(question.getId(), Collections.emptyList())
              .stream()
              .sorted(Comparator.comparing(AssessmentVersionOption::getOptionNo))
              .toList()
      );
      questionList.add(item);
    }

    List<AssessmentVersionRule> rules = assessmentVersionRuleMapper.selectList(
        new LambdaQueryWrapper<AssessmentVersionRule>()
            .eq(AssessmentVersionRule::getVersionId, version.getId())
            .orderByAsc(AssessmentVersionRule::getMinScore)
    );

    Map<String, Object> result = new LinkedHashMap<>();
    result.put("scale", scale);
    result.put("version", version);
    result.put("questions", questionList);
    result.put("rules", rules);
    return result;
  }

  @Transactional
  public String updateScale(AssessmentScaleManageRequestMessage request) {
    AssessmentScale scale = assessmentScaleMapper.selectById(request.getScaleId());
    if (scale == null) {
      throw new RuntimeException("量表不存在");
    }

    if (request.getQuestions() == null || request.getQuestions().isEmpty()) {
      throw new RuntimeException("题目不能为空");
    }
    if (request.getRules() == null || request.getRules().isEmpty()) {
      throw new RuntimeException("规则不能为空");
    }

    AssessmentScaleVersion latestVersion = assessmentScaleVersionMapper.selectOne(
        new LambdaQueryWrapper<AssessmentScaleVersion>()
            .eq(AssessmentScaleVersion::getScaleId, scale.getId())
            .orderByDesc(AssessmentScaleVersion::getVersionNo)
            .last("limit 1")
    );
    if (latestVersion == null) {
      throw new RuntimeException("旧版本不存在");
    }

    int nextVersionNo = latestVersion.getVersionNo() + 1;

    AssessmentScaleVersion newVersion = new AssessmentScaleVersion();
    newVersion.setScaleId(scale.getId());
    newVersion.setVersionNo(nextVersionNo);
    newVersion.setVersionStatus(scale.getStatus() == 1 ? "ACTIVE" : "INACTIVE");
    newVersion.setVersionRemark(request.getVersionRemark());
    newVersion.setCreatedBy(request.getOperator());
    assessmentScaleVersionMapper.insert(newVersion);

    saveQuestionsAndRules(newVersion.getId(), request.getQuestions(), request.getRules());

    int scoreMin = request.getRules().stream()
        .mapToInt(AssessmentScaleUpdateRequest.RuleDTO::getMinScore)
        .min()
        .orElse(0);
    int scoreMax = request.getRules().stream()
        .mapToInt(AssessmentScaleUpdateRequest.RuleDTO::getMaxScore)
        .max()
        .orElse(0);

    scale.setScaleName(request.getScaleName());
    scale.setScaleType(request.getScaleType());
    scale.setDescription(request.getDescription());
    scale.setQuestionCount(request.getQuestions().size());
    scale.setScoreMin(scoreMin);
    scale.setScoreMax(scoreMax);
    scale.setCurrentVersionId(newVersion.getId());
    assessmentScaleMapper.updateById(scale);

    return "修改成功，已生成新版本";
  }

  @Transactional
  public String enableScale(Long scaleId) {
    AssessmentScale scale = assessmentScaleMapper.selectById(scaleId);
    if (scale == null) {
      throw new RuntimeException("量表不存在");
    }

    if (scale.getDeletedFlag() == 1) {
      throw new RuntimeException("量表已删除，不能启用");
    }

    scale.setStatus(1);
    assessmentScaleMapper.updateById(scale);

    if (scale.getCurrentVersionId() != null) {
      AssessmentScaleVersion version = assessmentScaleVersionMapper.selectById(scale.getCurrentVersionId());
      if (version == null) {
        throw new RuntimeException("当前版本不存在");
      }
      version.setVersionStatus("ACTIVE");
      assessmentScaleVersionMapper.updateById(version);
    }

    return "启用成功";
  }

  @Transactional
  public String disableScale(Long scaleId) {
    AssessmentScale scale = assessmentScaleMapper.selectById(scaleId);
    if (scale == null) {
      throw new RuntimeException("量表不存在");
    }

    scale.setStatus(0);
    assessmentScaleMapper.updateById(scale);

    if (scale.getCurrentVersionId() != null) {
      AssessmentScaleVersion version = assessmentScaleVersionMapper.selectById(scale.getCurrentVersionId());
      if (version == null) {
        throw new RuntimeException("当前版本不存在");
      }
      version.setVersionStatus("INACTIVE");
      assessmentScaleVersionMapper.updateById(version);
    }

    return "停用成功";
  }

  @Transactional
  public String deleteScale(Long scaleId) {
    AssessmentScale scale = assessmentScaleMapper.selectById(scaleId);
    if (scale == null) {
      throw new RuntimeException("量表不存在");
    }

    scale.setDeletedFlag(1);
    scale.setStatus(0);
    assessmentScaleMapper.updateById(scale);

    if (scale.getCurrentVersionId() != null) {
      AssessmentScaleVersion version = assessmentScaleVersionMapper.selectById(scale.getCurrentVersionId());
      if (version == null) {
        throw new RuntimeException("当前版本不存在");
      }
      version.setVersionStatus("INACTIVE");
      assessmentScaleVersionMapper.updateById(version);
    }

    return "删除成功（逻辑删除）";
  }

  private void saveQuestionsAndRules(
      Long versionId,
      List<AssessmentScaleUpdateRequest.QuestionDTO> questionDTOList,
      List<AssessmentScaleUpdateRequest.RuleDTO> ruleDTOList) {

    for (AssessmentScaleUpdateRequest.QuestionDTO questionDTO : questionDTOList) {
      AssessmentVersionQuestion question = new AssessmentVersionQuestion();
      question.setVersionId(versionId);
      question.setQuestionNo(questionDTO.getQuestionNo());
      question.setQuestionText(questionDTO.getQuestionText());
      question.setRequiredFlag(1);
      assessmentVersionQuestionMapper.insert(question);

      if (questionDTO.getOptions() != null) {
        for (AssessmentScaleUpdateRequest.OptionDTO optionDTO : questionDTO.getOptions()) {
          AssessmentVersionOption option = new AssessmentVersionOption();
          option.setVersionQuestionId(question.getId());
          option.setOptionNo(optionDTO.getOptionNo());
          option.setOptionText(optionDTO.getOptionText());
          option.setOptionScore(optionDTO.getOptionScore());
          assessmentVersionOptionMapper.insert(option);
        }
      }
    }

    for (AssessmentScaleUpdateRequest.RuleDTO ruleDTO : ruleDTOList) {
      AssessmentVersionRule rule = new AssessmentVersionRule();
      rule.setVersionId(versionId);
      rule.setMinScore(ruleDTO.getMinScore());
      rule.setMaxScore(ruleDTO.getMaxScore());
      rule.setResultLevel(ruleDTO.getResultLevel());
      rule.setResultSummary(ruleDTO.getResultSummary());
      rule.setSuggestion(ruleDTO.getSuggestion());
      assessmentVersionRuleMapper.insert(rule);
    }
  }
}