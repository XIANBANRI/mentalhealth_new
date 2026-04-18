package com.sl.mentalhealth.controller;

import com.sl.mentalhealth.common.Result;
import com.sl.mentalhealth.dto.CounselorStudentQueryRequest;
import com.sl.mentalhealth.service.CounselorStudentGatewayService;
import com.sl.mentalhealth.vo.CounselorStudentDetailVO;
import com.sl.mentalhealth.vo.CounselorStudentPageVO;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/counselor/student")
@RequiredArgsConstructor
public class CounselorStudentController {

  private final CounselorStudentGatewayService counselorStudentGatewayService;

  /**
   * 获取当前辅导员管理的班级列表
   */
  @GetMapping("/classes")
  public Result<List<String>> classes(@RequestParam String counselorAccount) {
    try {
      List<String> result = counselorStudentGatewayService.listManagedClasses(counselorAccount);
      return Result.success("查询班级列表成功", result);
    } catch (Exception e) {
      return Result.error("查询班级列表失败：" + e.getMessage());
    }
  }

  /**
   * 分页查询当前辅导员所管理班级下的学生
   */
  @PostMapping("/list")
  public Result<CounselorStudentPageVO> list(@RequestBody CounselorStudentQueryRequest request) {
    try {
      CounselorStudentPageVO result = counselorStudentGatewayService.listStudents(request);
      return Result.success("查询学生列表成功", result);
    } catch (Exception e) {
      return Result.error("查询学生列表失败：" + e.getMessage());
    }
  }

  /**
   * 查询学生详情 + 学生心理测试汇总记录
   */
  @GetMapping("/detail")
  public Result<CounselorStudentDetailVO> detail(@RequestParam String counselorAccount,
      @RequestParam String studentId) {
    try {
      CounselorStudentDetailVO result =
          counselorStudentGatewayService.getStudentDetail(counselorAccount, studentId);
      return Result.success("查询学生详情成功", result);
    } catch (Exception e) {
      return Result.error("查询学生详情失败：" + e.getMessage());
    }
  }
}