package com.sl.mentalhealth.kafka.message;

import com.sl.mentalhealth.vo.AdminProfileResponseVO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AdminProfileResponseMessage {

  /**
   * 对应请求ID
   */
  private String requestId;

  /**
   * 是否成功
   */
  private boolean success;

  /**
   * 返回消息
   */
  private String message;

  /**
   * 返回数据
   */
  private AdminProfileResponseVO data;
}