package com.sl.mentalhealth.controller;

import com.sl.mentalhealth.dto.StudentProfileRequest;
import com.sl.mentalhealth.service.AvatarStorageService;
import com.sl.mentalhealth.service.StudentProfileGatewayService;
import com.sl.mentalhealth.vo.StudentProfileResponseVO;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/student")
public class StudentProfileController {

  private final StudentProfileGatewayService studentProfileGatewayService;
  private final AvatarStorageService avatarStorageService;

  public StudentProfileController(StudentProfileGatewayService studentProfileGatewayService,
      AvatarStorageService avatarStorageService) {
    this.studentProfileGatewayService = studentProfileGatewayService;
    this.avatarStorageService = avatarStorageService;
  }

  @PostMapping("/profile")
  public ResponseEntity<Map<String, Object>> queryProfile(
      @RequestBody StudentProfileRequest request) {

    Map<String, Object> result = new HashMap<>();

    try {
      StudentProfileResponseVO data = studentProfileGatewayService.queryProfile(request);
      result.put("success", true);
      result.put("message", "查询成功");
      result.put("data", data);
      return ResponseEntity.ok(result);
    } catch (Exception e) {
      result.put("success", false);
      result.put("message", e.getMessage() == null ? "查询失败" : e.getMessage());
      return ResponseEntity.badRequest().body(result);
    }
  }

  @PostMapping(value = "/profile/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<Map<String, Object>> uploadAvatar(
      @RequestParam("studentId") String studentId,
      @RequestParam("file") MultipartFile file) {

    Map<String, Object> result = new HashMap<>();
    AvatarStorageService.StorageResult storageResult = null;
    String oldAvatarUrl = null;

    try {
      // 先查当前头像，记录旧头像地址
      StudentProfileRequest profileRequest = new StudentProfileRequest();
      profileRequest.setStudentId(studentId);
      StudentProfileResponseVO oldProfile = studentProfileGatewayService.queryProfile(profileRequest);
      if (oldProfile != null) {
        oldAvatarUrl = oldProfile.getAvatarUrl();
      }

      // 保存新头像文件
      storageResult = avatarStorageService.saveStudentAvatar(studentId, file);

      // 通过 Kafka 更新数据库里的 avatar_url
      StudentProfileResponseVO data = studentProfileGatewayService.updateAvatar(
          studentId, storageResult.getAvatarUrl());

      // 数据库更新成功后，再删除旧头像
      if (oldAvatarUrl != null
          && !oldAvatarUrl.isBlank()
          && !oldAvatarUrl.equals(storageResult.getAvatarUrl())) {
        avatarStorageService.deleteByUrl(oldAvatarUrl);
      }

      result.put("success", true);
      result.put("message", "头像上传成功");
      result.put("data", data);
      return ResponseEntity.ok(result);
    } catch (Exception e) {
      if (storageResult != null) {
        avatarStorageService.deleteByUrl(storageResult.getAvatarUrl());
      }
      result.put("success", false);
      result.put("message", e.getMessage() == null ? "头像上传失败" : e.getMessage());
      return ResponseEntity.badRequest().body(result);
    }
  }
}