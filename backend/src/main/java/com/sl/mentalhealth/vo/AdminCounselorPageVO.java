package com.sl.mentalhealth.vo;

import java.util.List;

public class AdminCounselorPageVO {

  private Long total;
  private Integer pageNum;
  private Integer pageSize;
  private List<AdminCounselorVO> list;

  public Long getTotal() {
    return total;
  }

  public void setTotal(Long total) {
    this.total = total;
  }

  public Integer getPageNum() {
    return pageNum;
  }

  public void setPageNum(Integer pageNum) {
    this.pageNum = pageNum;
  }

  public Integer getPageSize() {
    return pageSize;
  }

  public void setPageSize(Integer pageSize) {
    this.pageSize = pageSize;
  }

  public List<AdminCounselorVO> getList() {
    return list;
  }

  public void setList(List<AdminCounselorVO> list) {
    this.list = list;
  }
}