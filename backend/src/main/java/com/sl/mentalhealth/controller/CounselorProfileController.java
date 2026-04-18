package com.sl.mentalhealth.controller;

import com.sl.mentalhealth.common.Result;
import com.sl.mentalhealth.dto.CounselorProfileRequest;
import com.sl.mentalhealth.service.AvatarStorageService;
import com.sl.mentalhealth.service.CounselorProfileGatewayService;
import com.sl.mentalhealth.vo.CounselorProfileResponseVO;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/counselor")
@CrossOrigin
public class
CounselorProfileController {

  private final CounselorProfileGatewayService counselorProfileGatewayService;
  private final AvatarStorageService avatarStorageService;

  public CounselorProfileController(CounselorProfileGatewayService counselorProfileGatewayService,
      AvatarStorageService avatarStorageService) {
    this.counselorProfileGatewayService = counselorProfileGatewayService;
    this.avatarStorageService = avatarStorageService;
  }

  @PostMapping("/profile")
  public Result<?> getProfile(@RequestBody CounselorProfileRequest request) {
    if (request == null || request.getAccount() == null || request.getAccount().trim().isEmpty()) {
      return Result.error("辅导员账号不能为空");
    }

    CounselorProfileResponseVO data =
        counselorProfileGatewayService.getProfile(request.getAccount().trim());

    return Result.success("查询成功", data);
  }

  @PostMapping(value = "/profile/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public Result<?> uploadAvatar(@RequestParam("account") String account,
      @RequestParam("file") MultipartFile file) {

    if (account == null || account.trim().isEmpty()) {
      return Result.error("辅导员账号不能为空");
    }

    AvatarStorageService.StorageResult storageResult = null;
    String oldAvatarUrl = null;

    try {
      CounselorProfileResponseVO oldProfile =
          counselorProfileGatewayService.getProfile(account.trim());
      if (oldProfile != null) {
        oldAvatarUrl = oldProfile.getAvatarUrl();
      }

      storageResult = avatarStorageService.saveCounselorAvatar(account.trim(), file);

      CounselorProfileResponseVO data =
          counselorProfileGatewayService.updateAvatar(account.trim(), storageResult.getAvatarUrl());

      if (oldAvatarUrl != null
          && !oldAvatarUrl.isBlank()
          && !oldAvatarUrl.equals(storageResult.getAvatarUrl())) {
        avatarStorageService.deleteByUrl(oldAvatarUrl);
      }

      return Result.success("头像上传成功", data);
    } catch (Exception e) {
      if (storageResult != null) {
        avatarStorageService.deleteByUrl(storageResult.getAvatarUrl());
      }
      return Result.error(e.getMessage() == null ? "头像上传失败" : e.getMessage());
    }
  }
}