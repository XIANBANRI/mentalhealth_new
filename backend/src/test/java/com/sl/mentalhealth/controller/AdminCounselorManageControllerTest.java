package com.sl.mentalhealth.controller;

import com.sl.mentalhealth.common.Result;
import com.sl.mentalhealth.dto.AdminCounselorClassesUpdateRequest;
import com.sl.mentalhealth.dto.AdminCounselorCreateRequest;
import com.sl.mentalhealth.dto.AdminCounselorQueryRequest;
import com.sl.mentalhealth.dto.AdminCounselorUpdateRequest;
import com.sl.mentalhealth.service.AdminCounselorManageGatewayService;
import com.sl.mentalhealth.vo.AdminCounselorDetailVO;
import com.sl.mentalhealth.vo.AdminCounselorPageVO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminCounselorManageControllerTest {

  @Mock
  private AdminCounselorManageGatewayService gatewayService;

  @InjectMocks
  private AdminCounselorManageController controller;

  @Test
  void page_whenRequestIsNull_shouldUseNewRequest() {
    AdminCounselorPageVO pageVO = mock(AdminCounselorPageVO.class);
    when(gatewayService.queryPage(any(AdminCounselorQueryRequest.class))).thenReturn(pageVO);

    Result<AdminCounselorPageVO> result = controller.page(null);

    assertEquals(200, result.getCode());
    assertEquals("查询成功", result.getMessage());
    assertSame(pageVO, result.getData());

    ArgumentCaptor<AdminCounselorQueryRequest> captor =
        ArgumentCaptor.forClass(AdminCounselorQueryRequest.class);
    verify(gatewayService).queryPage(captor.capture());
    assertNotNull(captor.getValue());
  }

  @Test
  void page_success() {
    AdminCounselorQueryRequest request = mock(AdminCounselorQueryRequest.class);
    AdminCounselorPageVO pageVO = mock(AdminCounselorPageVO.class);

    when(gatewayService.queryPage(request)).thenReturn(pageVO);

    Result<AdminCounselorPageVO> result = controller.page(request);

    assertEquals(200, result.getCode());
    assertEquals("查询成功", result.getMessage());
    assertSame(pageVO, result.getData());
  }

  @Test
  void detail_success() {
    AdminCounselorDetailVO detailVO = mock(AdminCounselorDetailVO.class);
    when(gatewayService.detail("c001")).thenReturn(detailVO);

    Result<AdminCounselorDetailVO> result = controller.detail("c001");

    assertEquals(200, result.getCode());
    assertEquals("查询成功", result.getMessage());
    assertSame(detailVO, result.getData());
  }

  @Test
  void create_success() {
    AdminCounselorCreateRequest request = mock(AdminCounselorCreateRequest.class);
    AdminCounselorDetailVO detailVO = mock(AdminCounselorDetailVO.class);

    when(gatewayService.create(request)).thenReturn(detailVO);

    Result<AdminCounselorDetailVO> result = controller.create(request);

    assertEquals(200, result.getCode());
    assertEquals("新增成功", result.getMessage());
    assertSame(detailVO, result.getData());
  }

  @Test
  void update_success() {
    AdminCounselorUpdateRequest request = mock(AdminCounselorUpdateRequest.class);
    AdminCounselorDetailVO detailVO = mock(AdminCounselorDetailVO.class);

    when(gatewayService.update(request)).thenReturn(detailVO);

    Result<AdminCounselorDetailVO> result = controller.update(request);

    assertEquals(200, result.getCode());
    assertEquals("修改成功", result.getMessage());
    assertSame(detailVO, result.getData());
  }

  @Test
  void updateClasses_success() {
    AdminCounselorClassesUpdateRequest request = mock(AdminCounselorClassesUpdateRequest.class);
    AdminCounselorDetailVO detailVO = mock(AdminCounselorDetailVO.class);

    when(gatewayService.updateClasses(request)).thenReturn(detailVO);

    Result<AdminCounselorDetailVO> result = controller.updateClasses(request);

    assertEquals(200, result.getCode());
    assertEquals("班级更新成功", result.getMessage());
    assertSame(detailVO, result.getData());
  }
}