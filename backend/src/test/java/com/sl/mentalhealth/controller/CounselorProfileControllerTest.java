package com.sl.mentalhealth.controller;

import com.sl.mentalhealth.common.Result;
import com.sl.mentalhealth.dto.CounselorProfileRequest;
import com.sl.mentalhealth.service.AvatarStorageService;
import com.sl.mentalhealth.service.CounselorProfileGatewayService;
import com.sl.mentalhealth.vo.CounselorProfileResponseVO;
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
class CounselorProfileControllerTest {

  @Mock
  private CounselorProfileGatewayService counselorProfileGatewayService;

  @Mock
  private AvatarStorageService avatarStorageService;

  @InjectMocks
  private CounselorProfileController controller;

  @Test
  void getProfile_whenRequestNull_shouldReturnError() {
    Result<?> result = controller.getProfile(null);

    assertEquals(500, result.getCode());
    assertEquals("辅导员账号不能为空", result.getMessage());
    assertNull(result.getData());

    verifyNoInteractions(counselorProfileGatewayService);
  }

  @Test
  void getProfile_whenAccountBlank_shouldReturnError() {
    CounselorProfileRequest request = mock(CounselorProfileRequest.class);
    when(request.getAccount()).thenReturn("   ");

    Result<?> result = controller.getProfile(request);

    assertEquals(500, result.getCode());
    assertEquals("辅导员账号不能为空", result.getMessage());
    assertNull(result.getData());

    verifyNoInteractions(counselorProfileGatewayService);
  }

  @Test
  void getProfile_success() {
    CounselorProfileRequest request = mock(CounselorProfileRequest.class);
    CounselorProfileResponseVO vo = mock(CounselorProfileResponseVO.class);

    when(request.getAccount()).thenReturn(" counselor1 ");
    when(counselorProfileGatewayService.getProfile("counselor1")).thenReturn(vo);

    Result<?> result = controller.getProfile(request);

    assertEquals(200, result.getCode());
    assertEquals("查询成功", result.getMessage());
    assertSame(vo, result.getData());
  }

  @Test
  void uploadAvatar_whenAccountBlank_shouldReturnError() {
    MockMultipartFile file =
        new MockMultipartFile("file", "avatar.png", "image/png", new byte[]{1});

    Result<?> result = controller.uploadAvatar("   ", file);

    assertEquals(500, result.getCode());
    assertEquals("辅导员账号不能为空", result.getMessage());
    assertNull(result.getData());

    verifyNoInteractions(counselorProfileGatewayService, avatarStorageService);
  }

  @Test
  void uploadAvatar_success_shouldDeleteOldAvatarWhenChanged() {
    MockMultipartFile file =
        new MockMultipartFile("file", "avatar.png", "image/png", new byte[]{1, 2, 3});

    CounselorProfileResponseVO oldProfile = mock(CounselorProfileResponseVO.class);
    when(oldProfile.getAvatarUrl()).thenReturn("/avatar/counselor/old.png");

    AvatarStorageService.StorageResult storageResult =
        mock(AvatarStorageService.StorageResult.class);
    when(storageResult.getAvatarUrl()).thenReturn("/avatar/counselor/new.png");

    CounselorProfileResponseVO updatedProfile = mock(CounselorProfileResponseVO.class);

    when(counselorProfileGatewayService.getProfile("c001")).thenReturn(oldProfile);
    when(avatarStorageService.saveCounselorAvatar(eq("c001"), eq(file))).thenReturn(storageResult);
    when(counselorProfileGatewayService.updateAvatar("c001", "/avatar/counselor/new.png"))
        .thenReturn(updatedProfile);

    Result<?> result = controller.uploadAvatar(" c001 ", file);

    assertEquals(200, result.getCode());
    assertEquals("头像上传成功", result.getMessage());
    assertSame(updatedProfile, result.getData());

    verify(avatarStorageService).deleteByUrl("/avatar/counselor/old.png");
  }

  @Test
  void uploadAvatar_whenUpdateFails_shouldRollbackNewAvatar() {
    MockMultipartFile file =
        new MockMultipartFile("file", "avatar.png", "image/png", new byte[]{1, 2, 3});

    CounselorProfileResponseVO oldProfile = mock(CounselorProfileResponseVO.class);
    when(oldProfile.getAvatarUrl()).thenReturn("/avatar/counselor/old.png");

    AvatarStorageService.StorageResult storageResult =
        mock(AvatarStorageService.StorageResult.class);
    when(storageResult.getAvatarUrl()).thenReturn("/avatar/counselor/new.png");

    when(counselorProfileGatewayService.getProfile("c001")).thenReturn(oldProfile);
    when(avatarStorageService.saveCounselorAvatar(eq("c001"), eq(file))).thenReturn(storageResult);
    when(counselorProfileGatewayService.updateAvatar("c001", "/avatar/counselor/new.png"))
        .thenThrow(new RuntimeException("更新头像失败"));

    Result<?> result = controller.uploadAvatar("c001", file);

    assertEquals(500, result.getCode());
    assertEquals("更新头像失败", result.getMessage());
    assertNull(result.getData());

    verify(avatarStorageService).deleteByUrl("/avatar/counselor/new.png");
  }
}