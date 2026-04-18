package com.sl.mentalhealth.controller;

import com.sl.mentalhealth.common.Result;
import com.sl.mentalhealth.dto.CounselorStudentQueryRequest;
import com.sl.mentalhealth.service.CounselorStudentGatewayService;
import com.sl.mentalhealth.vo.CounselorStudentDetailVO;
import com.sl.mentalhealth.vo.CounselorStudentPageVO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CounselorStudentControllerTest {

  @Mock
  private CounselorStudentGatewayService counselorStudentGatewayService;

  @InjectMocks
  private CounselorStudentController controller;

  @Test
  void classes_success() {
    List<String> classes = Arrays.asList("软件1班", "软件2班");
    when(counselorStudentGatewayService.listManagedClasses("c001")).thenReturn(classes);

    Result<List<String>> result = controller.classes("c001");

    assertEquals(200, result.getCode());
    assertEquals("查询班级列表成功", result.getMessage());
    assertSame(classes, result.getData());
  }

  @Test
  void classes_whenException_shouldReturnError() {
    when(counselorStudentGatewayService.listManagedClasses("c001"))
        .thenThrow(new RuntimeException("服务异常"));

    Result<List<String>> result = controller.classes("c001");

    assertEquals(500, result.getCode());
    assertEquals("查询班级列表失败：服务异常", result.getMessage());
    assertNull(result.getData());
  }

  @Test
  void list_success() {
    CounselorStudentQueryRequest request = mock(CounselorStudentQueryRequest.class);
    CounselorStudentPageVO pageVO = mock(CounselorStudentPageVO.class);

    when(counselorStudentGatewayService.listStudents(request)).thenReturn(pageVO);

    Result<CounselorStudentPageVO> result = controller.list(request);

    assertEquals(200, result.getCode());
    assertEquals("查询学生列表成功", result.getMessage());
    assertSame(pageVO, result.getData());
  }

  @Test
  void list_whenException_shouldReturnError() {
    CounselorStudentQueryRequest request = mock(CounselorStudentQueryRequest.class);

    when(counselorStudentGatewayService.listStudents(request))
        .thenThrow(new RuntimeException("服务异常"));

    Result<CounselorStudentPageVO> result = controller.list(request);

    assertEquals(500, result.getCode());
    assertEquals("查询学生列表失败：服务异常", result.getMessage());
    assertNull(result.getData());
  }

  @Test
  void detail_success() {
    CounselorStudentDetailVO detailVO = mock(CounselorStudentDetailVO.class);

    when(counselorStudentGatewayService.getStudentDetail("c001", "s001")).thenReturn(detailVO);

    Result<CounselorStudentDetailVO> result = controller.detail("c001", "s001");

    assertEquals(200, result.getCode());
    assertEquals("查询学生详情成功", result.getMessage());
    assertSame(detailVO, result.getData());
  }

  @Test
  void detail_whenException_shouldReturnError() {
    when(counselorStudentGatewayService.getStudentDetail("c001", "s001"))
        .thenThrow(new RuntimeException("服务异常"));

    Result<CounselorStudentDetailVO> result = controller.detail("c001", "s001");

    assertEquals(500, result.getCode());
    assertEquals("查询学生详情失败：服务异常", result.getMessage());
    assertNull(result.getData());
  }
}