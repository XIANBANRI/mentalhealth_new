package com.sl.mentalhealth.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.sl.mentalhealth.dto.AssessmentScaleUpdateRequest;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.List;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

class AssessmentScaleExcelParserServiceTest {

  private final AssessmentScaleExcelParserService service = new AssessmentScaleExcelParserService();

  @Test
  void parseQuestionExcel_success() throws Exception {
    XSSFWorkbook workbook = new XSSFWorkbook();
    Sheet sheet = workbook.createSheet();

    Row header = sheet.createRow(0);
    header.createCell(0).setCellValue("题号");
    header.createCell(1).setCellValue("项目名称");
    header.createCell(2).setCellValue("0分");
    header.createCell(3).setCellValue("1分");
    header.createCell(4).setCellValue("2分");

    Row row1 = sheet.createRow(1);
    row1.createCell(0).setCellValue(1);
    row1.createCell(1).setCellValue("我最近情绪稳定");
    row1.createCell(2).setCellValue("从不");
    row1.createCell(3).setCellValue("有时");
    row1.createCell(4).setCellValue("经常");

    MockMultipartFile file = toMultipartFile(workbook, "question.xlsx");

    List<AssessmentScaleUpdateRequest.QuestionDTO> result = service.parseQuestionExcel(file);

    assertEquals(1, result.size());
    AssessmentScaleUpdateRequest.QuestionDTO question = result.get(0);
    assertEquals(1, question.getQuestionNo());
    assertEquals("我最近情绪稳定", question.getQuestionText());
    assertEquals(3, question.getOptions().size());
    assertEquals(1, question.getOptions().get(0).getOptionNo());
    assertEquals("从不", question.getOptions().get(0).getOptionText());
    assertEquals(0, question.getOptions().get(0).getOptionScore());
    assertEquals(2, question.getOptions().get(2).getOptionScore());
  }

  @Test
  void parseQuestionExcel_invalidHeader_throwsException() throws Exception {
    XSSFWorkbook workbook = new XSSFWorkbook();
    Sheet sheet = workbook.createSheet();
    Row header = sheet.createRow(0);
    header.createCell(0).setCellValue("序号");
    header.createCell(1).setCellValue("项目名称");
    header.createCell(2).setCellValue("0分");

    MockMultipartFile file = toMultipartFile(workbook, "question.xlsx");

    IllegalArgumentException ex =
        assertThrows(IllegalArgumentException.class, () -> service.parseQuestionExcel(file));

    assertEquals("题目模板第1行第1列应为“题号”", ex.getMessage());
  }

  @Test
  void parseQuestionExcel_duplicateScoreColumn_throwsException() throws Exception {
    XSSFWorkbook workbook = new XSSFWorkbook();
    Sheet sheet = workbook.createSheet();
    Row header = sheet.createRow(0);
    header.createCell(0).setCellValue("题号");
    header.createCell(1).setCellValue("项目名称");
    header.createCell(2).setCellValue("0分");
    header.createCell(3).setCellValue("0分");

    MockMultipartFile file = toMultipartFile(workbook, "question.xlsx");

    IllegalArgumentException ex =
        assertThrows(IllegalArgumentException.class, () -> service.parseQuestionExcel(file));

    assertEquals("题目模板第1行存在重复分值列：0分", ex.getMessage());
  }

  @Test
  void parseRuleExcel_success() throws Exception {
    XSSFWorkbook workbook = new XSSFWorkbook();
    Sheet sheet = workbook.createSheet();

    Row header = sheet.createRow(0);
    header.createCell(0).setCellValue("最低分数");
    header.createCell(1).setCellValue("最高分数");
    header.createCell(2).setCellValue("结果等级");
    header.createCell(3).setCellValue("结果说明");
    header.createCell(4).setCellValue("建议内容");

    Row row1 = sheet.createRow(1);
    row1.createCell(0).setCellValue(0);
    row1.createCell(1).setCellValue(9);
    row1.createCell(2).setCellValue("正常");
    row1.createCell(3).setCellValue("状态正常");
    row1.createCell(4).setCellValue("继续保持");

    Row row2 = sheet.createRow(2);
    row2.createCell(0).setCellValue(10);
    row2.createCell(1).setCellValue(19);
    row2.createCell(2).setCellValue("轻度");
    row2.createCell(3).setCellValue("需要关注");
    row2.createCell(4).setCellValue("建议咨询");

    MockMultipartFile file = toMultipartFile(workbook, "rule.xlsx");

    List<AssessmentScaleUpdateRequest.RuleDTO> result = service.parseRuleExcel(file);

    assertEquals(2, result.size());
    assertEquals(0, result.get(0).getMinScore());
    assertEquals(19, result.get(1).getMaxScore());
    assertEquals("轻度", result.get(1).getResultLevel());
  }

  @Test
  void validateRuleList_overlap_throwsException() {
    AssessmentScaleUpdateRequest.RuleDTO r1 = new AssessmentScaleUpdateRequest.RuleDTO();
    r1.setMinScore(0);
    r1.setMaxScore(9);
    r1.setResultLevel("正常");
    r1.setResultSummary("正常");

    AssessmentScaleUpdateRequest.RuleDTO r2 = new AssessmentScaleUpdateRequest.RuleDTO();
    r2.setMinScore(9);
    r2.setMaxScore(19);
    r2.setResultLevel("轻度");
    r2.setResultSummary("关注");

    IllegalArgumentException ex =
        assertThrows(IllegalArgumentException.class, () -> service.validateRuleList(Arrays.asList(r1, r2)));

    assertEquals("第2条规则不符合要求：当前最低分数必须大于上一条规则的最高分数（上一条最高分数为 9）", ex.getMessage());
  }

  private MockMultipartFile toMultipartFile(XSSFWorkbook workbook, String filename) throws Exception {
    try (workbook; ByteArrayOutputStream out = new ByteArrayOutputStream()) {
      workbook.write(out);
      return new MockMultipartFile(
          "file",
          filename,
          "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
          out.toByteArray());
    }
  }
}
