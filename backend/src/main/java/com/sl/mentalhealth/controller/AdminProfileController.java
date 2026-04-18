package com.sl.mentalhealth.controller;

import com.sl.mentalhealth.common.Result;
import com.sl.mentalhealth.config.UserContext;
import com.sl.mentalhealth.service.AdminProfileGatewayService;
import com.sl.mentalhealth.service.AvatarStorageService;
import com.sl.mentalhealth.vo.AdminProfileResponseVO;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/admin")
public class AdminProfileController {

  private final AdminProfileGatewayService adminProfileGatewayService;
  private final AvatarStorageService avatarStorageService;

  public AdminProfileController(AdminProfileGatewayService adminProfileGatewayService,
      AvatarStorageService avatarStorageService) {
    this.adminProfileGatewayService = adminProfileGatewayService;
    this.avatarStorageService = avatarStorageService;
  }

  @GetMapping("/profile")
  public Result<AdminProfileResponseVO> getProfile() {
    try {
      String account = UserContext.getUsername();
      AdminProfileResponseVO vo = adminProfileGatewayService.getAdminProfile(account);
      return Result.success("查询管理员信息成功", vo);
    } catch (Exception e) {
      return Result.error(e.getMessage());
    }
  }

  @PostMapping(value = "/profile/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public Result<AdminProfileResponseVO> uploadAvatar(@RequestParam("file") MultipartFile file) {
    AvatarStorageService.StorageResult storageResult = null;
    String oldAvatarUrl = null;

    try {
      String account = UserContext.getUsername();

      AdminProfileResponseVO oldProfile = adminProfileGatewayService.getAdminProfile(account);
      if (oldProfile != null) {
        oldAvatarUrl = oldProfile.getAvatarUrl();
      }

      storageResult = avatarStorageService.saveAdminAvatar(account, file);

      AdminProfileResponseVO data =
          adminProfileGatewayService.updateAvatar(account, storageResult.getAvatarUrl());

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