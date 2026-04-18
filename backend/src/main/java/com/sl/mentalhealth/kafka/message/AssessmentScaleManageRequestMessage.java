package com.sl.mentalhealth.kafka.message;

import com.sl.mentalhealth.dto.AssessmentScaleUpdateRequest;
import java.util.List;

public class AssessmentScaleManageRequestMessage {

  private String requestId;
  private String action;

  private Long scaleId;
  private String scaleCode;
  private String scaleName;
  private String scaleType;
  private String description;
  private String operator;
  private String versionRemark;

  private String questionFileName;
  private String ruleFileName;

  private List<AssessmentScaleUpdateRequest.QuestionDTO> questions;
  private List<AssessmentScaleUpdateRequest.RuleDTO> rules;

  public String getRequestId() {
    return requestId;
  }

  public void setRequestId(String requestId) {
    this.requestId = requestId;
  }

  public String getAction() {
    return action;
  }

  public void setAction(String action) {
    this.action = action;
  }

  public Long getScaleId() {
    return scaleId;
  }

  public void setScaleId(Long scaleId) {
    this.scaleId = scaleId;
  }

  public String getScaleCode() {
    return scaleCode;
  }

  public void setScaleCode(String scaleCode) {
    this.scaleCode = scaleCode;
  }

  public String getScaleName() {
    return scaleName;
  }

  public void setScaleName(String scaleName) {
    this.scaleName = scaleName;
  }

  public String getScaleType() {
    return scaleType;
  }

  public void setScaleType(String scaleType) {
    this.scaleType = scaleType;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getOperator() {
    return operator;
  }

  public void setOperator(String operator) {
    this.operator = operator;
  }

  public String getVersionRemark() {
    return versionRemark;
  }

  public void setVersionRemark(String versionRemark) {
    this.versionRemark = versionRemark;
  }

  public String getQuestionFileName() {
    return questionFileName;
  }

  public void setQuestionFileName(String questionFileName) {
    this.questionFileName = questionFileName;
  }

  public String getRuleFileName() {
    return ruleFileName;
  }

  public void setRuleFileName(String ruleFileName) {
    this.ruleFileName = ruleFileName;
  }

  public List<AssessmentScaleUpdateRequest.QuestionDTO> getQuestions() {
    return questions;
  }

  public void setQuestions(List<AssessmentScaleUpdateRequest.QuestionDTO> questions) {
    this.questions = questions;
  }

  public List<AssessmentScaleUpdateRequest.RuleDTO> getRules() {
    return rules;
  }

  public void setRules(List<AssessmentScaleUpdateRequest.RuleDTO> rules) {
    this.rules = rules;
  }
}