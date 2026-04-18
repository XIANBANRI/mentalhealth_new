package com.sl.mentalhealth.controller;

import com.sl.mentalhealth.common.Result;
import com.sl.mentalhealth.config.LoginUser;
import com.sl.mentalhealth.config.UserContext;
import com.sl.mentalhealth.kafka.message.TeacherProfileResponseMessage;
import com.sl.mentalhealth.service.AvatarStorageService;
import com.sl.mentalhealth.service.TeacherProfileGatewayService;
import com.sl.mentalhealth.vo.TeacherProfileResponseVO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeacherProfileControllerTest {

  @Mock
  private TeacherProfileGatewayService teacherProfileGatewayService;

  @Mock
  private AvatarStorageService avatarStorageService;

  @InjectMocks
  private TeacherProfileController controller;

  @AfterEach
  void tearDown() {
    UserContext.clear();
  }

  @Test
  void getTeacherProfile_whenAccountBlank_shouldReturnBadRequest() {
    UserContext.set(new LoginUser("   ", "teacher"));

    Result<?> result = controller.getTeacherProfile();

    assertEquals(400, result.getCode());
    assertEquals("老师账号不能为空", result.getMessage());
    assertNull(result.getData());
  }

  @Test
  void getTeacherProfile_success() {
    UserContext.set(new LoginUser("t001", "teacher"));

    TeacherProfileResponseMessage response = mock(TeacherProfileResponseMessage.class);
    TeacherProfileResponseVO vo = mock(TeacherProfileResponseVO.class);

    when(response.isSuccess()).thenReturn(true);
    when(response.getMessage()).thenReturn("查询成功");
    when(response.getData()).thenReturn(vo);
    when(teacherProfileGatewayService.getTeacherProfile("t001")).thenReturn(response);

    Result<?> result = controller.getTeacherProfile();

    assertEquals(200, result.getCode());
    assertEquals("查询成功", result.getMessage());
    assertSame(vo, result.getData());
  }

  @Test
  void uploadAvatar_whenTeacherAccountBlank_shouldReturnBadRequest() {
    UserContext.set(new LoginUser("   ", "teacher"));

    MockMultipartFile file =
        new MockMultipartFile("file", "avatar.png", "image/png", new byte[]{1});

    Result<?> result = controller.uploadAvatar(file);

    assertEquals(400, result.getCode());
    assertEquals("老师账号不能为空", result.getMessage());
    assertNull(result.getData());
  }

  @Test
  void uploadAvatar_whenOldProfileQueryFailed_shouldReturnError() {
    UserContext.set(new LoginUser("t001", "teacher"));

    MockMultipartFile file =
        new MockMultipartFile("file", "avatar.png", "image/png", new byte[]{1});

    TeacherProfileResponseMessage oldResponse = mock(TeacherProfileResponseMessage.class);
    when(oldResponse.isSuccess()).thenReturn(false);
    when(oldResponse.getMessage()).thenReturn("查询旧头像失败");
    when(teacherProfileGatewayService.getTeacherProfile("t001")).thenReturn(oldResponse);

    Result<?> result = controller.uploadAvatar(file);

    assertEquals(500, result.getCode());
    assertEquals("查询旧头像失败", result.getMessage());
    assertNull(result.getData());

    verify(avatarStorageService, never()).saveTeacherAvatar(anyString(), any());
  }

  @Test
  void uploadAvatar_success_shouldDeleteOldAvatar() {
    UserContext.set(new LoginUser("t001", "teacher"));

    MockMultipartFile file =
        new MockMultipartFile("file", "avatar.png", "image/png", new byte[]{1, 2, 3});

    TeacherProfileResponseVO oldData = mock(TeacherProfileResponseVO.class);
    when(oldData.getAvatarUrl()).thenReturn("/avatar/teacher/old.png");

    TeacherProfileResponseMessage oldResponse = mock(TeacherProfileResponseMessage.class);
    when(oldResponse.isSuccess()).thenReturn(true);
    when(oldResponse.getData()).thenReturn(oldData);

    AvatarStorageService.StorageResult storageResult = mock(AvatarStorageService.StorageResult.class);
    when(storageResult.getAvatarUrl()).thenReturn("/avatar/teacher/new.png");

    TeacherProfileResponseVO newData = mock(TeacherProfileResponseVO.class);
    TeacherProfileResponseMessage updateResponse = mock(TeacherProfileResponseMessage.class);
    when(updateResponse.isSuccess()).thenReturn(true);
    when(updateResponse.getMessage()).thenReturn("头像上传成功");
    when(updateResponse.getData()).thenReturn(newData);

    when(teacherProfileGatewayService.getTeacherProfile("t001")).thenReturn(oldResponse);
    when(avatarStorageService.saveTeacherAvatar(eq("t001"), eq(file))).thenReturn(storageResult);
    when(teacherProfileGatewayService.updateAvatar("t001", "/avatar/teacher/new.png"))
        .thenReturn(updateResponse);

    Result<?> result = controller.uploadAvatar(file);

    assertEquals(200, result.getCode());
    assertEquals("头像上传成功", result.getMessage());
    assertSame(newData, result.getData());

    verify(avatarStorageService).deleteByUrl("/avatar/teacher/old.png");
  }

  @Test
  void uploadAvatar_whenUpdateResponseFailed_shouldRollbackNewAvatar() {
    UserContext.set(new LoginUser("t001", "teacher"));

    MockMultipartFile file =
        new MockMultipartFile("file", "avatar.png", "image/png", new byte[]{1, 2, 3});

    TeacherProfileResponseVO oldData = mock(TeacherProfileResponseVO.class);
    when(oldData.getAvatarUrl()).thenReturn("/avatar/teacher/old.png");

    TeacherProfileResponseMessage oldResponse = mock(TeacherProfileResponseMessage.class);
    when(oldResponse.isSuccess()).thenReturn(true);
    when(oldResponse.getData()).thenReturn(oldData);

    AvatarStorageService.StorageResult storageResult = mock(AvatarStorageService.StorageResult.class);
    when(storageResult.getAvatarUrl()).thenReturn("/avatar/teacher/new.png");

    TeacherProfileResponseMessage updateResponse = mock(TeacherProfileResponseMessage.class);
    when(updateResponse.isSuccess()).thenReturn(false);
    when(updateResponse.getMessage()).thenReturn("数据库更新失败");

    when(teacherProfileGatewayService.getTeacherProfile("t001")).thenReturn(oldResponse);
    when(avatarStorageService.saveTeacherAvatar(eq("t001"), eq(file))).thenReturn(storageResult);
    when(teacherProfileGatewayService.updateAvatar("t001", "/avatar/teacher/new.png"))
        .thenReturn(updateResponse);

    Result<?> result = controller.uploadAvatar(file);

    assertEquals(500, result.getCode());
    assertEquals("数据库更新失败", result.getMessage());
    assertNull(result.getData());

    verify(avatarStorageService).deleteByUrl("/avatar/teacher/new.png");
  }

  @Test
  void uploadAvatar_whenException_shouldRollbackNewAvatar() {
    UserContext.set(new LoginUser("t001", "teacher"));

    MockMultipartFile file =
        new MockMultipartFile("file", "avatar.png", "image/png", new byte[]{1, 2, 3});

    TeacherProfileResponseVO oldData = mock(TeacherProfileResponseVO.class);
    when(oldData.getAvatarUrl()).thenReturn("/avatar/teacher/old.png");

    TeacherProfileResponseMessage oldResponse = mock(TeacherProfileResponseMessage.class);
    when(oldResponse.isSuccess()).thenReturn(true);
    when(oldResponse.getData()).thenReturn(oldData);

    AvatarStorageService.StorageResult storageResult = mock(AvatarStorageService.StorageResult.class);
    when(storageResult.getAvatarUrl()).thenReturn("/avatar/teacher/new.png");

    when(teacherProfileGatewayService.getTeacherProfile("t001")).thenReturn(oldResponse);
    when(avatarStorageService.saveTeacherAvatar(eq("t001"), eq(file))).thenReturn(storageResult);
    when(teacherProfileGatewayService.updateAvatar("t001", "/avatar/teacher/new.png"))
        .thenThrow(new RuntimeException("更新异常"));

    Result<?> result = controller.uploadAvatar(file);

    assertEquals(500, result.getCode());
    assertEquals("更新异常", result.getMessage());
    assertNull(result.getData());

    verify(avatarStorageService).deleteByUrl("/avatar/teacher/new.png");
  }
}