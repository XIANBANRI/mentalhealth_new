package com.sl.mentalhealth.controller;

import com.sl.mentalhealth.common.Result;
import com.sl.mentalhealth.dto.CounselorWarningQueryRequest;
import com.sl.mentalhealth.service.CounselorWarningGatewayService;
import com.sl.mentalhealth.service.LocalCounselorWarningService;
import com.sl.mentalhealth.vo.CounselorWarningDetailVO;
import com.sl.mentalhealth.vo.CounselorWarningPageVO;
import com.sl.mentalhealth.vo.CounselorWarningStudentVO;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/counselor/warning")
@RequiredArgsConstructor
public class CounselorWarningController {

  private final CounselorWarningGatewayService counselorWarningGatewayService;
  private final LocalCounselorWarningService localCounselorWarningService;

  @GetMapping("/classes")
  public Result<List<String>> classes(@RequestParam String counselorAccount) {
    try {
      List<String> result = counselorWarningGatewayService.listManagedClasses(counselorAccount);
      return Result.success("查询班级列表成功", result);
    } catch (Exception e) {
      return Result.error("查询班级列表失败：" + e.getMessage());
    }
  }

  @PostMapping("/list")
  public Result<CounselorWarningPageVO> list(@RequestBody CounselorWarningQueryRequest request) {
    try {
      CounselorWarningPageVO result = counselorWarningGatewayService.listDangerousStudents(request);
      return Result.success("查询预警名单成功", result);
    } catch (Exception e) {
      return Result.error("查询预警名单失败：" + e.getMessage());
    }
  }

  @GetMapping("/detail")
  public Result<CounselorWarningDetailVO> detail(@RequestParam String counselorAccount,
      @RequestParam String studentId,
      @RequestParam String semester) {
    try {
      CounselorWarningDetailVO result = counselorWarningGatewayService
          .getDangerousStudentDetail(counselorAccount, studentId, semester);
      return Result.success("查询预警详情成功", result);
    } catch (Exception e) {
      return Result.error("查询预警详情失败：" + e.getMessage());
    }
  }

  @GetMapping("/export")
  public ResponseEntity<?> export(@RequestParam String counselorAccount,
      @RequestParam(required = false) String semester,
      @RequestParam(required = false, defaultValue = "") String className) {
    try {
      List<CounselorWarningStudentVO> rows =
          localCounselorWarningService.exportDangerousStudents(
              counselorAccount,
              semester,
              className
          );

      byte[] fileBytes = buildExcelBytes(rows);

      String safeSemester = StringUtils.hasText(semester) ? semester.trim() : "第1学期";
      String safeClassName = StringUtils.hasText(className) ? className.trim() : "全部班级";
      String fileName = "学生预警名单-" + safeSemester + "-" + safeClassName + ".xlsx";

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(
          MediaType.parseMediaType(
              "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
          )
      );
      headers.setContentDisposition(
          ContentDisposition.attachment()
              .filename(fileName, StandardCharsets.UTF_8)
              .build()
      );

      return ResponseEntity.ok()
          .headers(headers)
          .body(fileBytes);
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .contentType(MediaType.APPLICATION_JSON)
          .body(Result.error("导出预警名单失败：" + e.getMessage()));
    }
  }

  private byte[] buildExcelBytes(List<CounselorWarningStudentVO> rows) throws IOException {
    try (Workbook workbook = new XSSFWorkbook();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

      Sheet sheet = workbook.createSheet("预警名单");

      Font headerFont = workbook.createFont();
      headerFont.setBold(true);

      CellStyle headerStyle = workbook.createCellStyle();
      headerStyle.setFont(headerFont);
      headerStyle.setAlignment(HorizontalAlignment.CENTER);

      String[] headers = {"学号", "姓名", "班级", "学院", "联系电话"};

      Row headerRow = sheet.createRow(0);
      for (int i = 0; i < headers.length; i++) {
        Cell cell = headerRow.createCell(i);
        cell.setCellValue(headers[i]);
        cell.setCellStyle(headerStyle);
      }

      int rowIndex = 1;
      for (CounselorWarningStudentVO item : rows) {
        Row row = sheet.createRow(rowIndex++);
        row.createCell(0).setCellValue(defaultString(item.getStudentId()));
        row.createCell(1).setCellValue(defaultString(item.getName()));
        row.createCell(2).setCellValue(defaultString(item.getClassName()));
        row.createCell(3).setCellValue(defaultString(item.getCollege()));
        row.createCell(4).setCellValue(defaultString(item.getPhone()));
      }

      sheet.setColumnWidth(0, 18 * 256);
      sheet.setColumnWidth(1, 12 * 256);
      sheet.setColumnWidth(2, 24 * 256);
      sheet.setColumnWidth(3, 20 * 256);
      sheet.setColumnWidth(4, 18 * 256);

      workbook.write(outputStream);
      return outputStream.toByteArray();
    }
  }

  private String defaultString(String value) {
    return value == null ? "" : value;
  }
}