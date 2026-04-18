package com.sl.mentalhealth.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sl.mentalhealth.entity.Admin;
import com.sl.mentalhealth.mapper.AdminMapper;
import com.sl.mentalhealth.vo.AdminProfileResponseVO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LocalAdminProfileServiceTest {

    @Mock
    private AdminMapper adminMapper;

    @InjectMocks
    private LocalAdminProfileService service;

    @Test
    void getAdminProfile_success() {
        Admin admin = new Admin();
        admin.setAccount("admin001");
        admin.setName("系统管理员");
        admin.setAvatarUrl("/files/avatar/admin/a.png");

        when(adminMapper.selectById("admin001")).thenReturn(admin);

        AdminProfileResponseVO result = service.getAdminProfile("admin001");

        assertEquals("admin001", result.getAccount());
        assertEquals("系统管理员", result.getName());
        assertEquals("/files/avatar/admin/a.png", result.getAvatarUrl());
    }

    @Test
    void getAdminProfile_blankAccount_throwsIllegalArgumentException() {
        IllegalArgumentException ex =
            assertThrows(IllegalArgumentException.class, () -> service.getAdminProfile("  "));

        assertEquals("管理员账号不能为空", ex.getMessage());
    }

    @Test
    void updateAvatar_success() {
        Admin admin = new Admin();
        admin.setAccount("admin001");
        admin.setName("系统管理员");

        when(adminMapper.selectById("admin001")).thenReturn(admin);
        when(adminMapper.updateById(admin)).thenReturn(1);

        AdminProfileResponseVO result =
            service.updateAvatar("admin001", " /files/avatar/admin/new.png ");

        assertEquals("/files/avatar/admin/new.png", result.getAvatarUrl());
        verify(adminMapper).updateById(admin);
    }

    @Test
    void updateAvatar_adminNotFound_throwsRuntimeException() {
        when(adminMapper.selectById("admin001")).thenReturn(null);

        RuntimeException ex =
            assertThrows(
                RuntimeException.class,
                () -> service.updateAvatar("admin001", "/files/avatar/admin/new.png"));

        assertEquals("管理员不存在", ex.getMessage());
    }

    @Test
    void updateAvatar_blankAvatar_throwsIllegalArgumentException() {
        IllegalArgumentException ex =
            assertThrows(
                IllegalArgumentException.class,
                () -> service.updateAvatar("admin001", " "));

        assertEquals("头像地址不能为空", ex.getMessage());
    }
}