package com.sl.mentalhealth.controller;

import com.sl.mentalhealth.common.Result;
import com.sl.mentalhealth.dto.AdminTeacherCreateRequest;
import com.sl.mentalhealth.dto.AdminTeacherQueryRequest;
import com.sl.mentalhealth.dto.AdminTeacherUpdateRequest;
import com.sl.mentalhealth.service.AdminTeacherManageGatewayService;
import com.sl.mentalhealth.vo.AdminTeacherPageVO;
import com.sl.mentalhealth.vo.AdminTeacherVO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminTeacherManageControllerTest {

  @Mock
  private AdminTeacherManageGatewayService gatewayService;

  @InjectMocks
  private AdminTeacherManageController controller;

  @Test
  void page_whenRequestIsNull_shouldUseNewRequest() {
    AdminTeacherPageVO pageVO = mock(AdminTeacherPageVO.class);
    when(gatewayService.queryPage(any(AdminTeacherQueryRequest.class))).thenReturn(pageVO);

    Result<AdminTeacherPageVO> result = controller.page(null);

    assertEquals(200, result.getCode());
    assertEquals("查询成功", result.getMessage());
    assertSame(pageVO, result.getData());

    ArgumentCaptor<AdminTeacherQueryRequest> captor =
        ArgumentCaptor.forClass(AdminTeacherQueryRequest.class);
    verify(gatewayService).queryPage(captor.capture());
    assertNotNull(captor.getValue());
  }

  @Test
  void page_success() {
    AdminTeacherQueryRequest request = mock(AdminTeacherQueryRequest.class);
    AdminTeacherPageVO pageVO = mock(AdminTeacherPageVO.class);

    when(gatewayService.queryPage(request)).thenReturn(pageVO);

    Result<AdminTeacherPageVO> result = controller.page(request);

    assertEquals(200, result.getCode());
    assertEquals("查询成功", result.getMessage());
    assertSame(pageVO, result.getData());
  }

  @Test
  void detail_success() {
    AdminTeacherVO vo = mock(AdminTeacherVO.class);
    when(gatewayService.detail("t001")).thenReturn(vo);

    Result<AdminTeacherVO> result = controller.detail("t001");

    assertEquals(200, result.getCode());
    assertEquals("查询成功", result.getMessage());
    assertSame(vo, result.getData());
  }

  @Test
  void create_success() {
    AdminTeacherCreateRequest request = mock(AdminTeacherCreateRequest.class);
    AdminTeacherVO vo = mock(AdminTeacherVO.class);

    when(gatewayService.create(request)).thenReturn(vo);

    Result<AdminTeacherVO> result = controller.create(request);

    assertEquals(200, result.getCode());
    assertEquals("新增成功", result.getMessage());
    assertSame(vo, result.getData());
  }

  @Test
  void update_success() {
    AdminTeacherUpdateRequest request = mock(AdminTeacherUpdateRequest.class);
    AdminTeacherVO vo = mock(AdminTeacherVO.class);

    when(gatewayService.update(request)).thenReturn(vo);

    Result<AdminTeacherVO> result = controller.update(request);

    assertEquals(200, result.getCode());
    assertEquals("修改成功", result.getMessage());
    assertSame(vo, result.getData());
  }
}