package com.sl.mentalhealth.service;

import com.sl.mentalhealth.entity.Admin;
import com.sl.mentalhealth.mapper.AdminMapper;
import com.sl.mentalhealth.vo.AdminProfileResponseVO;
import org.springframework.stereotype.Service;

@Service
public class LocalAdminProfileService {

  private final AdminMapper adminMapper;

  public LocalAdminProfileService(AdminMapper adminMapper) {
    this.adminMapper = adminMapper;
  }

  public AdminProfileResponseVO getAdminProfile(String account) {
    if (account == null || account.trim().isEmpty()) {
      throw new IllegalArgumentException("管理员账号不能为空");
    }

    Admin admin = adminMapper.selectById(account.trim());
    if (admin == null) {
      throw new RuntimeException("管理员不存在");
    }

    return buildVO(admin);
  }

  public AdminProfileResponseVO updateAvatar(String account, String avatarUrl) {
    if (account == null || account.trim().isEmpty()) {
      throw new IllegalArgumentException("管理员账号不能为空");
    }
    if (avatarUrl == null || avatarUrl.trim().isEmpty()) {
      throw new IllegalArgumentException("头像地址不能为空");
    }

    Admin admin = adminMapper.selectById(account.trim());
    if (admin == null) {
      throw new RuntimeException("管理员不存在");
    }

    admin.setAvatarUrl(avatarUrl.trim());
    adminMapper.updateById(admin);

    return buildVO(admin);
  }

  private AdminProfileResponseVO buildVO(Admin admin) {
    AdminProfileResponseVO vo = new AdminProfileResponseVO();
    vo.setAccount(admin.getAccount());
    vo.setName(admin.getName());
    vo.setAvatarUrl(admin.getAvatarUrl());
    return vo;
  }
}