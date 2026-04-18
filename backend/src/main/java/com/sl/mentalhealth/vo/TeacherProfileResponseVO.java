package com.sl.mentalhealth.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeacherProfileResponseVO {

  private String teacherAccount;
  private String teacherName;
  private String phone;
  private String officeLocation;
  private String avatarUrl;
}