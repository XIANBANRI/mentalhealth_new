package com.sl.mentalhealth.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

class AvatarStorageServiceTest {

    private AvatarStorageService service;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        service = new AvatarStorageService();
        ReflectionTestUtils.setField(service, "avatarRootDir", tempDir.toString());
    }

    @Test
    void saveAdminAvatar_success() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "avatar.png",
                "image/png",
                "abc".getBytes()
        );

        AvatarStorageService.StorageResult result = service.saveAdminAvatar("admin001", file);

        assertNotNull(result);
        assertTrue(result.getAvatarUrl().startsWith("/files/avatar/admin/admin001_"));
        assertTrue(result.getAvatarUrl().endsWith(".png"));
        assertTrue(result.getAbsolutePath().contains("admin"));
        assertTrue(Files.exists(Path.of(result.getAbsolutePath())));
    }

    @Test
    void saveStudentAvatar_blankStudentId_throwsRuntimeException() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "avatar.jpg",
                "image/jpeg",
                "abc".getBytes()
        );

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.saveStudentAvatar("  ", file));

        assertEquals("学号不能为空", ex.getMessage());
    }

    @Test
    void saveTeacherAvatar_invalidExtension_throwsRuntimeException() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "avatar.gif",
                "image/gif",
                "abc".getBytes()
        );

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.saveTeacherAvatar("t001", file));

        assertEquals("只支持 jpg、jpeg、png、webp 格式图片", ex.getMessage());
    }

    @Test
    void saveCounselorAvatar_nonImageContentType_throwsRuntimeException() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "avatar.png",
                "text/plain",
                "abc".getBytes()
        );

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.saveCounselorAvatar("c001", file));

        assertEquals("上传文件必须是图片", ex.getMessage());
    }

    @Test
    void deleteByUrl_existingFile_deletesFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "avatar.webp",
                "image/webp",
                "abc".getBytes()
        );

        AvatarStorageService.StorageResult result = service.saveAdminAvatar("admin001", file);
        Path saved = Path.of(result.getAbsolutePath());
        assertTrue(Files.exists(saved));

        service.deleteByUrl(result.getAvatarUrl());

        assertFalse(Files.exists(saved));
    }
}
