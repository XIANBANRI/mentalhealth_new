package com.sl.mentalhealth.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class AvatarStorageService {

  private static final long MAX_SIZE = 5 * 1024 * 1024L;
  private static final Set<String> ALLOWED_EXTENSIONS =
      Set.of(".jpg", ".jpeg", ".png", ".webp");

  @Value("${app.upload.avatar-root-dir:E:/mental_health/backend/uploads/avatar}")
  private String avatarRootDir;

  public StorageResult saveStudentAvatar(String studentId, MultipartFile file) {
    return storeAvatar("student", studentId, file, "学号不能为空");
  }

  public StorageResult saveTeacherAvatar(String teacherAccount, MultipartFile file) {
    return storeAvatar("teacher", teacherAccount, file, "老师账号不能为空");
  }

  public StorageResult saveCounselorAvatar(String account, MultipartFile file) {
    return storeAvatar("counselor", account, file, "辅导员账号不能为空");
  }

  public StorageResult saveAdminAvatar(String account, MultipartFile file) {
    return storeAvatar("admin", account, file, "管理员账号不能为空");
  }

  private StorageResult storeAvatar(String roleFolder, String ownerId, MultipartFile file,
      String emptyMessage) {
    if (ownerId == null || ownerId.trim().isEmpty()) {
      throw new RuntimeException(emptyMessage);
    }
    validate(file);

    String extension = getExtension(file.getOriginalFilename());
    String fileName = ownerId.trim() + "_" + System.currentTimeMillis() + "_"
        + UUID.randomUUID().toString().replace("-", "").substring(0, 12)
        + extension;

    Path targetDir = Paths.get(avatarRootDir, roleFolder).toAbsolutePath().normalize();
    Path targetFile = targetDir.resolve(fileName).normalize();

    try {
      Files.createDirectories(targetDir);
      file.transferTo(targetFile.toFile());

      return new StorageResult(
          "/files/avatar/" + roleFolder + "/" + fileName,
          targetFile.toString()
      );
    } catch (IOException e) {
      throw new RuntimeException("头像保存失败", e);
    }
  }

  public void deleteByUrl(String avatarUrl) {
    if (avatarUrl == null || avatarUrl.isBlank()) {
      return;
    }

    String prefix = "/files/avatar/";
    if (!avatarUrl.startsWith(prefix)) {
      return;
    }

    String relativePath = avatarUrl.substring(prefix.length());
    Path filePath = Paths.get(avatarRootDir).resolve(relativePath).normalize();

    try {
      Files.deleteIfExists(filePath);
    } catch (IOException ignored) {
    }
  }

  private void validate(MultipartFile file) {
    if (file == null || file.isEmpty()) {
      throw new RuntimeException("请选择要上传的头像图片");
    }

    if (file.getSize() > MAX_SIZE) {
      throw new RuntimeException("头像大小不能超过 5MB");
    }

    String extension = getExtension(file.getOriginalFilename());
    if (!ALLOWED_EXTENSIONS.contains(extension)) {
      throw new RuntimeException("只支持 jpg、jpeg、png、webp 格式图片");
    }

    String contentType = file.getContentType();
    if (contentType == null || !contentType.toLowerCase(Locale.ROOT).startsWith("image/")) {
      throw new RuntimeException("上传文件必须是图片");
    }
  }

  private String getExtension(String originalFilename) {
    if (originalFilename == null || !originalFilename.contains(".")) {
      throw new RuntimeException("文件名不合法");
    }

    String extension = originalFilename.substring(originalFilename.lastIndexOf("."))
        .toLowerCase(Locale.ROOT);

    if (!ALLOWED_EXTENSIONS.contains(extension)) {
      throw new RuntimeException("只支持 jpg、jpeg、png、webp 格式图片");
    }

    return extension;
  }

  public static class StorageResult {

    private final String avatarUrl;
    private final String absolutePath;

    public StorageResult(String avatarUrl, String absolutePath) {
      this.avatarUrl = avatarUrl;
      this.absolutePath = absolutePath;
    }

    public String getAvatarUrl() {
      return avatarUrl;
    }

    public String getAbsolutePath() {
      return absolutePath;
    }
  }
}