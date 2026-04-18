package com.sl.mentalhealth.controller;

import com.sl.mentalhealth.common.Result;
import com.sl.mentalhealth.config.LoginUser;
import com.sl.mentalhealth.config.UserContext;
import com.sl.mentalhealth.service.AdminProfileGatewayService;
import com.sl.mentalhealth.service.AvatarStorageService;
import com.sl.mentalhealth.vo.AdminProfileResponseVO;
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
class AdminProfileControllerTest {

  @Mock
  private AdminProfileGatewayService adminProfileGatewayService;

  @Mock
  private AvatarStorageService avatarStorageService;

  @InjectMocks
  private AdminProfileController controller;

  @AfterEach
  void tearDown() {
    UserContext.clear();
  }

  @Test
  void getProfile_success() {
    UserContext.set(new LoginUser("admin", "admin"));

    AdminProfileResponseVO vo = mock(AdminProfileResponseVO.class);
    when(adminProfileGatewayService.getAdminProfile("admin")).thenReturn(vo);

    Result<AdminProfileResponseVO> result = controller.getProfile();

    assertEquals(200, result.getCode());
    assertEquals("查询管理员信息成功", result.getMessage());
    assertSame(vo, result.getData());
  }

  @Test
  void getProfile_whenException_shouldReturnError() {
    UserContext.set(new LoginUser("admin", "admin"));

    when(adminProfileGatewayService.getAdminProfile("admin"))
        .thenThrow(new RuntimeException("查询失败"));

    Result<AdminProfileResponseVO> result = controller.getProfile();

    assertEquals(500, result.getCode());
    assertEquals("查询失败", result.getMessage());
    assertNull(result.getData());
  }

  @Test
  void uploadAvatar_success_shouldDeleteOldAvatarWhenChanged() {
    UserContext.set(new LoginUser("admin", "admin"));

    MockMultipartFile file =
        new MockMultipartFile("file", "avatar.png", "image/png", new byte[]{1, 2, 3});

    AdminProfileResponseVO oldProfile = mock(AdminProfileResponseVO.class);
    when(oldProfile.getAvatarUrl()).thenReturn("/avatar/admin/old.png");

    AvatarStorageService.StorageResult storageResult =
        mock(AvatarStorageService.StorageResult.class);
    when(storageResult.getAvatarUrl()).thenReturn("/avatar/admin/new.png");

    AdminProfileResponseVO updatedProfile = mock(AdminProfileResponseVO.class);

    when(adminProfileGatewayService.getAdminProfile("admin")).thenReturn(oldProfile);
    when(avatarStorageService.saveAdminAvatar(eq("admin"), eq(file))).thenReturn(storageResult);
    when(adminProfileGatewayService.updateAvatar("admin", "/avatar/admin/new.png"))
        .thenReturn(updatedProfile);

    Result<AdminProfileResponseVO> result = controller.uploadAvatar(file);

    assertEquals(200, result.getCode());
    assertEquals("头像上传成功", result.getMessage());
    assertSame(updatedProfile, result.getData());

    verify(avatarStorageService).deleteByUrl("/avatar/admin/old.png");
  }

  @Test
  void uploadAvatar_whenNoOldAvatar_shouldNotDeleteOldFile() {
    UserContext.set(new LoginUser("admin", "admin"));

    MockMultipartFile file =
        new MockMultipartFile("file", "avatar.png", "image/png", new byte[]{1, 2, 3});

    AdminProfileResponseVO oldProfile = mock(AdminProfileResponseVO.class);
    when(oldProfile.getAvatarUrl()).thenReturn(null);

    AvatarStorageService.StorageResult storageResult =
        mock(AvatarStorageService.StorageResult.class);
    when(storageResult.getAvatarUrl()).thenReturn("/avatar/admin/new.png");

    AdminProfileResponseVO updatedProfile = mock(AdminProfileResponseVO.class);

    when(adminProfileGatewayService.getAdminProfile("admin")).thenReturn(oldProfile);
    when(avatarStorageService.saveAdminAvatar(eq("admin"), eq(file))).thenReturn(storageResult);
    when(adminProfileGatewayService.updateAvatar("admin", "/avatar/admin/new.png"))
        .thenReturn(updatedProfile);

    Result<AdminProfileResponseVO> result = controller.uploadAvatar(file);

    assertEquals(200, result.getCode());
    assertEquals("头像上传成功", result.getMessage());
    assertSame(updatedProfile, result.getData());

    verify(avatarStorageService, never()).deleteByUrl("/avatar/admin/old.png");
  }

  @Test
  void uploadAvatar_whenUpdateFails_shouldRollbackNewAvatar() {
    UserContext.set(new LoginUser("admin", "admin"));

    MockMultipartFile file =
        new MockMultipartFile("file", "avatar.png", "image/png", new byte[]{1, 2, 3});

    AdminProfileResponseVO oldProfile = mock(AdminProfileResponseVO.class);
    when(oldProfile.getAvatarUrl()).thenReturn("/avatar/admin/old.png");

    AvatarStorageService.StorageResult storageResult =
        mock(AvatarStorageService.StorageResult.class);
    when(storageResult.getAvatarUrl()).thenReturn("/avatar/admin/new.png");

    when(adminProfileGatewayService.getAdminProfile("admin")).thenReturn(oldProfile);
    when(avatarStorageService.saveAdminAvatar(eq("admin"), eq(file))).thenReturn(storageResult);
    when(adminProfileGatewayService.updateAvatar("admin", "/avatar/admin/new.png"))
        .thenThrow(new RuntimeException("更新头像失败"));

    Result<AdminProfileResponseVO> result = controller.uploadAvatar(file);

    assertEquals(500, result.getCode());
    assertEquals("更新头像失败", result.getMessage());
    assertNull(result.getData());

    verify(avatarStorageService).deleteByUrl("/avatar/admin/new.png");
  }
}