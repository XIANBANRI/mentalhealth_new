package com.sl.mentalhealth.service;

import com.sl.mentalhealth.dto.AssessmentScaleUpdateRequest;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class AssessmentScaleExcelParserService {

  private static final Pattern SCORE_HEADER_PATTERN = Pattern.compile("^(-?\\d+)\\s*分$");

  public List<AssessmentScaleUpdateRequest.QuestionDTO> parseQuestionExcel(MultipartFile file) throws Exception {
    List<AssessmentScaleUpdateRequest.QuestionDTO> result = new ArrayList<>();

    try (InputStream inputStream = file.getInputStream();
        Workbook workbook = WorkbookFactory.create(inputStream)) {

      Sheet sheet = workbook.getSheetAt(0);
      if (sheet == null) {
        throw new IllegalArgumentException("题目模板为空");
      }

      Row headerRow = sheet.getRow(0);
      validateQuestionHeader(headerRow);

      List<ScoreColumnMeta> scoreColumns = parseScoreColumns(headerRow);
      if (scoreColumns.isEmpty()) {
        throw new IllegalArgumentException("题目模板第1行缺少分值列，请从第3列开始填写“0分、1分、2分...”");
      }

      for (int i = 1; i <= sheet.getLastRowNum(); i++) {
        Row row = sheet.getRow(i);
        if (row == null || isRowCompletelyBlank(row, Math.max(7, headerRow.getLastCellNum()))) {
          continue;
        }

        Integer questionNo = getIntValue(row.getCell(0), "题目模板第" + (i + 1) + "行第1列“题号”");
        String questionText = trimToNull(getStringValue(row.getCell(1)));

        if (questionNo == null) {
          throw new IllegalArgumentException("题目模板第" + (i + 1) + "行第1列“题号”不能为空");
        }
        if (questionText == null) {
          throw new IllegalArgumentException("题目模板第" + (i + 1) + "行第2列“项目名称”不能为空");
        }

        AssessmentScaleUpdateRequest.QuestionDTO questionDTO = new AssessmentScaleUpdateRequest.QuestionDTO();
        questionDTO.setQuestionNo(questionNo);
        questionDTO.setQuestionText(questionText);

        List<AssessmentScaleUpdateRequest.OptionDTO> options = new ArrayList<>();
        int optionNo = 1;

        for (ScoreColumnMeta scoreColumn : scoreColumns) {
          String optionText = trimToNull(getStringValue(row.getCell(scoreColumn.getColumnIndex())));
          if (optionText == null || "—".equals(optionText) || "-".equals(optionText)) {
            continue;
          }

          AssessmentScaleUpdateRequest.OptionDTO optionDTO = new AssessmentScaleUpdateRequest.OptionDTO();
          optionDTO.setOptionNo(optionNo);
          optionDTO.setOptionText(optionText);
          optionDTO.setOptionScore(scoreColumn.getScore());
          options.add(optionDTO);
          optionNo++;
        }

        if (options.isEmpty()) {
          throw new IllegalArgumentException("题目模板第" + (i + 1) + "行没有可用选项，请至少填写一个分值列对应的选项内容");
        }

        questionDTO.setOptions(options);
        result.add(questionDTO);
      }
    }

    if (result.isEmpty()) {
      throw new IllegalArgumentException("题目模板中没有可导入的题目数据");
    }

    return result;
  }

  public List<AssessmentScaleUpdateRequest.RuleDTO> parseRuleExcel(MultipartFile file) throws Exception {
    List<AssessmentScaleUpdateRequest.RuleDTO> result = new ArrayList<>();
    List<Integer> sourceRows = new ArrayList<>();

    try (InputStream inputStream = file.getInputStream();
        Workbook workbook = WorkbookFactory.create(inputStream)) {

      Sheet sheet = workbook.getSheetAt(0);
      if (sheet == null) {
        throw new IllegalArgumentException("评分判定表为空");
      }

      Row headerRow = sheet.getRow(0);
      validateRuleHeader(headerRow);

      for (int i = 1; i <= sheet.getLastRowNum(); i++) {
        Row row = sheet.getRow(i);
        if (row == null || isRowCompletelyBlank(row, 5)) {
          continue;
        }

        Integer minScore = getIntValue(row.getCell(0), "评分判定表第" + (i + 1) + "行第1列“最低分数”");
        Integer maxScore = getIntValue(row.getCell(1), "评分判定表第" + (i + 1) + "行第2列“最高分数”");
        String resultLevel = trimToNull(getStringValue(row.getCell(2)));
        String resultSummary = trimToNull(getStringValue(row.getCell(3)));
        String suggestion = trimToNull(getStringValue(row.getCell(4)));

        if (minScore == null) {
          throw new IllegalArgumentException("评分判定表第" + (i + 1) + "行第1列“最低分数”不能为空");
        }
        if (maxScore == null) {
          throw new IllegalArgumentException("评分判定表第" + (i + 1) + "行第2列“最高分数”不能为空");
        }
        if (resultLevel == null) {
          throw new IllegalArgumentException("评分判定表第" + (i + 1) + "行第3列“结果等级”不能为空");
        }
        if (resultSummary == null) {
          throw new IllegalArgumentException("评分判定表第" + (i + 1) + "行第4列“结果说明”不能为空");
        }

        AssessmentScaleUpdateRequest.RuleDTO ruleDTO = new AssessmentScaleUpdateRequest.RuleDTO();
        ruleDTO.setMinScore(minScore);
        ruleDTO.setMaxScore(maxScore);
        ruleDTO.setResultLevel(resultLevel);
        ruleDTO.setResultSummary(resultSummary);
        ruleDTO.setSuggestion(suggestion == null ? "" : suggestion);

        result.add(ruleDTO);
        sourceRows.add(i + 1);
      }
    }

    validateRuleList(result, sourceRows);
    return result;
  }

  public void validateRuleList(List<AssessmentScaleUpdateRequest.RuleDTO> rules) {
    validateRuleList(rules, null);
  }

  private void validateRuleList(List<AssessmentScaleUpdateRequest.RuleDTO> rules, List<Integer> sourceRows) {
    if (rules == null || rules.isEmpty()) {
      throw new IllegalArgumentException("规则不能为空");
    }

    for (int i = 0; i < rules.size(); i++) {
      AssessmentScaleUpdateRequest.RuleDTO current = rules.get(i);
      String currentLabel = buildRulePositionLabel(i, sourceRows);

      if (current.getMinScore() == null) {
        throw new IllegalArgumentException(currentLabel + "的最低分数不能为空");
      }
      if (current.getMaxScore() == null) {
        throw new IllegalArgumentException(currentLabel + "的最高分数不能为空");
      }
      if (trimToNull(current.getResultLevel()) == null) {
        throw new IllegalArgumentException(currentLabel + "的结果等级不能为空");
      }
      if (trimToNull(current.getResultSummary()) == null) {
        throw new IllegalArgumentException(currentLabel + "的结果说明不能为空");
      }

      if (current.getMaxScore() < current.getMinScore()) {
        throw new IllegalArgumentException(currentLabel + "不符合要求：最高分数不能低于最低分数");
      }

      if (i > 0) {
        AssessmentScaleUpdateRequest.RuleDTO previous = rules.get(i - 1);
        if (current.getMinScore() <= previous.getMaxScore()) {
          throw new IllegalArgumentException(
              currentLabel + "不符合要求：当前最低分数必须大于上一条规则的最高分数（上一条最高分数为 "
                  + previous.getMaxScore() + "）");
        }
      }
    }
  }

  private String buildRulePositionLabel(int index, List<Integer> sourceRows) {
    if (sourceRows != null && index < sourceRows.size()) {
      return "评分判定表第" + sourceRows.get(index) + "行";
    }
    return "第" + (index + 1) + "条规则";
  }

  private void validateQuestionHeader(Row headerRow) {
    if (headerRow == null) {
      throw new IllegalArgumentException("题目模板缺少表头");
    }

    String header1 = trimToNull(getStringValue(headerRow.getCell(0)));
    String header2 = trimToNull(getStringValue(headerRow.getCell(1)));

    if (!"题号".equals(header1)) {
      throw new IllegalArgumentException("题目模板第1行第1列应为“题号”");
    }
    if (!"项目名称".equals(header2)) {
      throw new IllegalArgumentException("题目模板第1行第2列应为“项目名称”");
    }
  }

  private void validateRuleHeader(Row headerRow) {
    if (headerRow == null) {
      throw new IllegalArgumentException("评分判定表缺少表头");
    }

    checkHeaderCell(headerRow, 0, "最低分数", "评分判定表");
    checkHeaderCell(headerRow, 1, "最高分数", "评分判定表");
    checkHeaderCell(headerRow, 2, "结果等级", "评分判定表");
    checkHeaderCell(headerRow, 3, "结果说明", "评分判定表");
    checkHeaderCell(headerRow, 4, "建议内容", "评分判定表");
  }

  private void checkHeaderCell(Row headerRow, int columnIndex, String expected, String templateName) {
    String actual = trimToNull(getStringValue(headerRow.getCell(columnIndex)));
    if (!expected.equals(actual)) {
      throw new IllegalArgumentException(templateName + "第1行第" + (columnIndex + 1) + "列应为“" + expected + "”");
    }
  }

  private List<ScoreColumnMeta> parseScoreColumns(Row headerRow) {
    List<ScoreColumnMeta> scoreColumns = new ArrayList<>();
    short lastCellNum = headerRow.getLastCellNum();
    if (lastCellNum < 3) {
      return scoreColumns;
    }

    Integer previousScore = null;
    Set<Integer> usedScores = new HashSet<>();

    for (int c = 2; c < lastCellNum; c++) {
      String headerText = trimToNull(getStringValue(headerRow.getCell(c)));
      if (headerText == null) {
        continue;
      }

      Matcher matcher = SCORE_HEADER_PATTERN.matcher(headerText);
      if (!matcher.matches()) {
        throw new IllegalArgumentException("题目模板第1行第" + (c + 1) + "列格式不正确，应为“0分”“1分”这类分值列");
      }

      int score = Integer.parseInt(matcher.group(1));

      if (usedScores.contains(score)) {
        throw new IllegalArgumentException("题目模板第1行存在重复分值列：" + score + "分");
      }

      if (previousScore != null && score <= previousScore) {
        throw new IllegalArgumentException("题目模板第1行分值列顺序不正确，必须从左到右递增");
      }

      usedScores.add(score);
      previousScore = score;
      scoreColumns.add(new ScoreColumnMeta(c, score));
    }

    return scoreColumns;
  }

  private Integer getIntValue(Cell cell, String fieldName) {
    if (cell == null) {
      return null;
    }

    if (cell.getCellType() == CellType.NUMERIC) {
      double value = cell.getNumericCellValue();
      if (value != Math.floor(value)) {
        throw new IllegalArgumentException(fieldName + "必须是整数");
      }
      return (int) value;
    }

    String text = trimToNull(getStringValue(cell));
    if (text == null) {
      return null;
    }

    try {
      return Integer.parseInt(text);
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException(fieldName + "必须是整数");
    }
  }

  private String getStringValue(Cell cell) {
    if (cell == null) {
      return null;
    }

    if (cell.getCellType() == CellType.STRING) {
      return cell.getStringCellValue();
    }
    if (cell.getCellType() == CellType.NUMERIC) {
      double numericValue = cell.getNumericCellValue();
      if (numericValue == Math.floor(numericValue)) {
        return String.valueOf((int) numericValue);
      }
      return String.valueOf(numericValue);
    }
    if (cell.getCellType() == CellType.BOOLEAN) {
      return String.valueOf(cell.getBooleanCellValue());
    }
    return cell.toString();
  }

  private boolean isRowCompletelyBlank(Row row, int checkColumnCount) {
    for (int i = 0; i < checkColumnCount; i++) {
      String value = trimToNull(getStringValue(row.getCell(i)));
      if (value != null) {
        return false;
      }
    }
    return true;
  }

  private String trimToNull(String text) {
    if (text == null) {
      return null;
    }
    String trimmed = text.trim();
    return trimmed.isEmpty() ? null : trimmed;
  }

  private static class ScoreColumnMeta {
    private final int columnIndex;
    private final int score;

    public ScoreColumnMeta(int columnIndex, int score) {
      this.columnIndex = columnIndex;
      this.score = score;
    }

    public int getColumnIndex() {
      return columnIndex;
    }

    public int getScore() {
      return score;
    }
  }
}