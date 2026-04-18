package com.sl.mentalhealth.vo;

import java.util.List;

public class AdminTeacherPageVO {

  private Long total;
  private Integer pageNum;
  private Integer pageSize;
  private List<AdminTeacherVO> list;

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

  public List<AdminTeacherVO> getList() {
    return list;
  }

  public void setList(List<AdminTeacherVO> list) {
    this.list = list;
  }
}