package com.sl.mentalhealth.controller;

import com.sl.mentalhealth.dto.StudentProfileRequest;
import com.sl.mentalhealth.service.AvatarStorageService;
import com.sl.mentalhealth.service.StudentProfileGatewayService;
import com.sl.mentalhealth.vo.StudentProfileResponseVO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentProfileControllerTest {

  @Mock
  private StudentProfileGatewayService studentProfileGatewayService;

  @Mock
  private AvatarStorageService avatarStorageService;

  @InjectMocks
  private StudentProfileController controller;

  @Test
  void queryProfile_success() {
    StudentProfileRequest request = mock(StudentProfileRequest.class);
    StudentProfileResponseVO vo = mock(StudentProfileResponseVO.class);

    when(studentProfileGatewayService.queryProfile(request)).thenReturn(vo);

    ResponseEntity<Map<String, Object>> response = controller.queryProfile(request);

    assertEquals(200, response.getStatusCode().value());
    assertNotNull(response.getBody());
    assertEquals(true, response.getBody().get("success"));
    assertEquals("查询成功", response.getBody().get("message"));
    assertSame(vo, response.getBody().get("data"));
  }

  @Test
  void queryProfile_whenException_shouldReturnBadRequest() {
    StudentProfileRequest request = mock(StudentProfileRequest.class);

    when(studentProfileGatewayService.queryProfile(request))
        .thenThrow(new RuntimeException("查询失败"));

    ResponseEntity<Map<String, Object>> response = controller.queryProfile(request);

    assertEquals(400, response.getStatusCode().value());
    assertNotNull(response.getBody());
    assertEquals(false, response.getBody().get("success"));
    assertEquals("查询失败", response.getBody().get("message"));
  }

  @Test
  void uploadAvatar_success_shouldDeleteOldAvatar() {
    MockMultipartFile file =
        new MockMultipartFile("file", "avatar.png", "image/png", new byte[]{1, 2, 3});

    StudentProfileResponseVO oldProfile = mock(StudentProfileResponseVO.class);
    when(oldProfile.getAvatarUrl()).thenReturn("/avatar/student/old.png");

    AvatarStorageService.StorageResult storageResult = mock(AvatarStorageService.StorageResult.class);
    when(storageResult.getAvatarUrl()).thenReturn("/avatar/student/new.png");

    StudentProfileResponseVO updatedProfile = mock(StudentProfileResponseVO.class);

    when(studentProfileGatewayService.queryProfile(any(StudentProfileRequest.class))).thenReturn(oldProfile);
    when(avatarStorageService.saveStudentAvatar(eq("s001"), eq(file))).thenReturn(storageResult);
    when(studentProfileGatewayService.updateAvatar("s001", "/avatar/student/new.png"))
        .thenReturn(updatedProfile);

    ResponseEntity<Map<String, Object>> response = controller.uploadAvatar("s001", file);

    assertEquals(200, response.getStatusCode().value());
    assertNotNull(response.getBody());
    assertEquals(true, response.getBody().get("success"));
    assertEquals("头像上传成功", response.getBody().get("message"));
    assertSame(updatedProfile, response.getBody().get("data"));

    ArgumentCaptor<StudentProfileRequest> captor =
        ArgumentCaptor.forClass(StudentProfileRequest.class);
    verify(studentProfileGatewayService).queryProfile(captor.capture());
    assertEquals("s001", captor.getValue().getStudentId());

    verify(avatarStorageService).deleteByUrl("/avatar/student/old.png");
  }

  @Test
  void uploadAvatar_whenUpdateFails_shouldRollbackNewAvatar() {
    MockMultipartFile file =
        new MockMultipartFile("file", "avatar.png", "image/png", new byte[]{1, 2, 3});

    StudentProfileResponseVO oldProfile = mock(StudentProfileResponseVO.class);
    when(oldProfile.getAvatarUrl()).thenReturn("/avatar/student/old.png");

    AvatarStorageService.StorageResult storageResult = mock(AvatarStorageService.StorageResult.class);
    when(storageResult.getAvatarUrl()).thenReturn("/avatar/student/new.png");

    when(studentProfileGatewayService.queryProfile(any(StudentProfileRequest.class))).thenReturn(oldProfile);
    when(avatarStorageService.saveStudentAvatar(eq("s001"), eq(file))).thenReturn(storageResult);
    when(studentProfileGatewayService.updateAvatar("s001", "/avatar/student/new.png"))
        .thenThrow(new RuntimeException("更新头像失败"));

    ResponseEntity<Map<String, Object>> response = controller.uploadAvatar("s001", file);

    assertEquals(400, response.getStatusCode().value());
    assertNotNull(response.getBody());
    assertEquals(false, response.getBody().get("success"));
    assertEquals("更新头像失败", response.getBody().get("message"));

    verify(avatarStorageService).deleteByUrl("/avatar/student/new.png");
  }

  @Test
  void uploadAvatar_whenOldAvatarBlank_shouldNotDeleteOldAvatar() {
    MockMultipartFile file =
        new MockMultipartFile("file", "avatar.png", "image/png", new byte[]{1, 2, 3});

    StudentProfileResponseVO oldProfile = mock(StudentProfileResponseVO.class);
    when(oldProfile.getAvatarUrl()).thenReturn("  ");

    AvatarStorageService.StorageResult storageResult = mock(AvatarStorageService.StorageResult.class);
    when(storageResult.getAvatarUrl()).thenReturn("/avatar/student/new.png");

    StudentProfileResponseVO updatedProfile = mock(StudentProfileResponseVO.class);

    when(studentProfileGatewayService.queryProfile(any(StudentProfileRequest.class))).thenReturn(oldProfile);
    when(avatarStorageService.saveStudentAvatar(eq("s001"), eq(file))).thenReturn(storageResult);
    when(studentProfileGatewayService.updateAvatar("s001", "/avatar/student/new.png"))
        .thenReturn(updatedProfile);

    ResponseEntity<Map<String, Object>> response = controller.uploadAvatar("s001", file);

    assertEquals(200, response.getStatusCode().value());
    verify(avatarStorageService, never()).deleteByUrl("/avatar/student/old.png");
  }
}