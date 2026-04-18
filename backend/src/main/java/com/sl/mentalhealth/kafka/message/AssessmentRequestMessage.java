package com.sl.mentalhealth.kafka.message;

import com.sl.mentalhealth.dto.AssessmentSubmitAnswer;
import java.util.List;

public class AssessmentRequestMessage {

  private String requestId;
  private String action;
  private String studentId;
  private String semester;
  private Long scaleId;
  private Long versionId;
  private List<AssessmentSubmitAnswer> answers;

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

  public String getStudentId() {
    return studentId;
  }

  public void setStudentId(String studentId) {
    this.studentId = studentId;
  }

  public String getSemester() {
    return semester;
  }

  public void setSemester(String semester) {
    this.semester = semester;
  }

  public Long getScaleId() {
    return scaleId;
  }

  public void setScaleId(Long scaleId) {
    this.scaleId = scaleId;
  }

  public Long getVersionId() {
    return versionId;
  }

  public void setVersionId(Long versionId) {
    this.versionId = versionId;
  }

  public List<AssessmentSubmitAnswer> getAnswers() {
    return answers;
  }

  public void setAnswers(List<AssessmentSubmitAnswer> answers) {
    this.answers = answers;
  }
}
