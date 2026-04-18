package com.sl.mentalhealth.service;

import com.sl.mentalhealth.entity.Counselor;
import com.sl.mentalhealth.mapper.CounselorMapper;
import com.sl.mentalhealth.vo.CounselorProfileResponseVO;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class LocalCounselorProfileService {

  private final CounselorMapper counselorMapper;

  public LocalCounselorProfileService(CounselorMapper counselorMapper) {
    this.counselorMapper = counselorMapper;
  }

  public CounselorProfileResponseVO getProfile(String account) {
    if (!StringUtils.hasText(account)) {
      return null;
    }

    Counselor counselor = counselorMapper.selectById(account.trim());
    if (counselor == null) {
      return null;
    }

    return toVO(counselor);
  }

  public CounselorProfileResponseVO updateAvatar(String account, String avatarUrl) {
    if (!StringUtils.hasText(account)) {
      return null;
    }

    Counselor counselor = counselorMapper.selectById(account.trim());
    if (counselor == null) {
      return null;
    }

    counselor.setAvatarUrl(avatarUrl);
    counselorMapper.updateById(counselor);

    return toVO(counselor);
  }

  private CounselorProfileResponseVO toVO(Counselor counselor) {
    CounselorProfileResponseVO vo = new CounselorProfileResponseVO();
    vo.setCounselorId(counselor.getAccount());
    vo.setName(counselor.getName());
    vo.setCollege(counselor.getCollege());
    vo.setGrade(counselor.getGrade());
    vo.setPhone(counselor.getPhone());
    vo.setAvatarUrl(counselor.getAvatarUrl());
    return vo;
  }
}