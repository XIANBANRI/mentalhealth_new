package com.sl.mentalhealth.controller;

import com.sl.mentalhealth.common.Result;
import com.sl.mentalhealth.dto.TeacherProfileRequest;
import com.sl.mentalhealth.kafka.message.TeacherProfileResponseMessage;
import com.sl.mentalhealth.service.AvatarStorageService;
import com.sl.mentalhealth.service.TeacherProfileGatewayService;
import com.sl.mentalhealth.vo.TeacherProfileResponseVO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/teacher")
@RequiredArgsConstructor
@CrossOrigin
public class TeacherProfileController {

  private final TeacherProfileGatewayService teacherProfileGatewayService;
  private final AvatarStorageService avatarStorageService;

  @PostMapping("/profile")
  public Result<?> getTeacherProfile(@RequestBody TeacherProfileRequest request) {
    if (request == null || request.getTeacherAccount() == null
        || request.getTeacherAccount().trim().isEmpty()) {
      return Result.badRequest("老师账号不能为空");
    }

    TeacherProfileResponseMessage response =
        teacherProfileGatewayService.getTeacherProfile(request.getTeacherAccount());

    if (response.isSuccess()) {
      return Result.success(response.getMessage(), response.getData());
    }
    return Result.error(response.getMessage());
  }

  @PostMapping(value = "/profile/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public Result<?> uploadAvatar(@RequestParam("teacherAccount") String teacherAccount,
      @RequestParam("file") MultipartFile file) {

    if (teacherAccount == null || teacherAccount.trim().isEmpty()) {
      return Result.badRequest("老师账号不能为空");
    }

    AvatarStorageService.StorageResult storageResult = null;
    String oldAvatarUrl = null;

    try {
      TeacherProfileResponseMessage oldResponse =
          teacherProfileGatewayService.getTeacherProfile(teacherAccount);

      if (!oldResponse.isSuccess()) {
        return Result.error(oldResponse.getMessage());
      }

      TeacherProfileResponseVO oldData = oldResponse.getData();
      if (oldData != null) {
        oldAvatarUrl = oldData.getAvatarUrl();
      }

      storageResult = avatarStorageService.saveTeacherAvatar(teacherAccount, file);

      TeacherProfileResponseMessage updateResponse =
          teacherProfileGatewayService.updateAvatar(teacherAccount, storageResult.getAvatarUrl());

      if (!updateResponse.isSuccess()) {
        if (storageResult != null) {
          avatarStorageService.deleteByUrl(storageResult.getAvatarUrl());
        }
        return Result.error(updateResponse.getMessage());
      }

      if (oldAvatarUrl != null
          && !oldAvatarUrl.isBlank()
          && !oldAvatarUrl.equals(storageResult.getAvatarUrl())) {
        avatarStorageService.deleteByUrl(oldAvatarUrl);
      }

      return Result.success(updateResponse.getMessage(), updateResponse.getData());
    } catch (Exception e) {
      if (storageResult != null) {
        avatarStorageService.deleteByUrl(storageResult.getAvatarUrl());
      }
      return Result.error(e.getMessage() == null ? "头像上传失败" : e.getMessage());
    }
  }
}