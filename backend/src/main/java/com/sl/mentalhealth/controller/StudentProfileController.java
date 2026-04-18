package com.sl.mentalhealth.controller;

import com.sl.mentalhealth.config.UserContext;
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

  @GetMapping("/profile")
  public ResponseEntity<Map<String, Object>> queryProfile() {
    Map<String, Object> result = new HashMap<>();

    try {
      String studentId = UserContext.getUsername();
      StudentProfileRequest request = new StudentProfileRequest();
      request.setStudentId(studentId);

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
      @RequestParam("file") MultipartFile file) {

    Map<String, Object> result = new HashMap<>();
    AvatarStorageService.StorageResult storageResult = null;
    String oldAvatarUrl = null;

    try {
      String studentId = UserContext.getUsername();

      StudentProfileRequest profileRequest = new StudentProfileRequest();
      profileRequest.setStudentId(studentId);
      StudentProfileResponseVO oldProfile = studentProfileGatewayService.queryProfile(profileRequest);
      if (oldProfile != null) {
        oldAvatarUrl = oldProfile.getAvatarUrl();
      }

      storageResult = avatarStorageService.saveStudentAvatar(studentId, file);

      StudentProfileResponseVO data = studentProfileGatewayService.updateAvatar(
          studentId, storageResult.getAvatarUrl());

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