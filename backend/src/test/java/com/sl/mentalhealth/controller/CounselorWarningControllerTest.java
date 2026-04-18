package com.sl.mentalhealth.controller;

import com.sl.mentalhealth.common.Result;
import com.sl.mentalhealth.dto.CounselorWarningQueryRequest;
import com.sl.mentalhealth.service.CounselorWarningGatewayService;
import com.sl.mentalhealth.vo.CounselorWarningDetailVO;
import com.sl.mentalhealth.vo.CounselorWarningPageVO;
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
class CounselorWarningControllerTest {

  @Mock
  private CounselorWarningGatewayService counselorWarningGatewayService;

  @InjectMocks
  private CounselorWarningController controller;

  @Test
  void classes_success() {
    List<String> classes = Arrays.asList("软件1班", "软件2班");
    when(counselorWarningGatewayService.listManagedClasses("c001")).thenReturn(classes);

    Result<List<String>> result = controller.classes("c001");

    assertEquals(200, result.getCode());
    assertEquals("查询班级列表成功", result.getMessage());
    assertSame(classes, result.getData());
  }

  @Test
  void classes_whenException_shouldReturnError() {
    when(counselorWarningGatewayService.listManagedClasses("c001"))
        .thenThrow(new RuntimeException("服务异常"));

    Result<List<String>> result = controller.classes("c001");

    assertEquals(500, result.getCode());
    assertEquals("查询班级列表失败：服务异常", result.getMessage());
    assertNull(result.getData());
  }

  @Test
  void list_success() {
    CounselorWarningQueryRequest request = mock(CounselorWarningQueryRequest.class);
    CounselorWarningPageVO pageVO = mock(CounselorWarningPageVO.class);

    when(counselorWarningGatewayService.listDangerousStudents(request)).thenReturn(pageVO);

    Result<CounselorWarningPageVO> result = controller.list(request);

    assertEquals(200, result.getCode());
    assertEquals("查询预警名单成功", result.getMessage());
    assertSame(pageVO, result.getData());
  }

  @Test
  void list_whenException_shouldReturnError() {
    CounselorWarningQueryRequest request = mock(CounselorWarningQueryRequest.class);

    when(counselorWarningGatewayService.listDangerousStudents(request))
        .thenThrow(new RuntimeException("查询失败"));

    Result<CounselorWarningPageVO> result = controller.list(request);

    assertEquals(500, result.getCode());
    assertEquals("查询预警名单失败：查询失败", result.getMessage());
    assertNull(result.getData());
  }

  @Test
  void detail_success() {
    CounselorWarningDetailVO detailVO = mock(CounselorWarningDetailVO.class);

    when(counselorWarningGatewayService
        .getDangerousStudentDetail("c001", "s001", "2025-2026-1"))
        .thenReturn(detailVO);

    Result<CounselorWarningDetailVO> result =
        controller.detail("c001", "s001", "2025-2026-1");

    assertEquals(200, result.getCode());
    assertEquals("查询预警详情成功", result.getMessage());
    assertSame(detailVO, result.getData());
  }

  @Test
  void detail_whenException_shouldReturnError() {
    when(counselorWarningGatewayService
        .getDangerousStudentDetail("c001", "s001", "2025-2026-1"))
        .thenThrow(new RuntimeException("详情查询失败"));

    Result<CounselorWarningDetailVO> result =
        controller.detail("c001", "s001", "2025-2026-1");

    assertEquals(500, result.getCode());
    assertEquals("查询预警详情失败：详情查询失败", result.getMessage());
    assertNull(result.getData());
  }
}