package com.sl.mentalhealth.service;

import com.sl.mentalhealth.dto.CounselorTrendReportQueryRequest;
import com.sl.mentalhealth.mapper.StudentAssessmentSemesterSummaryMapper;
import com.sl.mentalhealth.mapper.result.ClassDangerCountResult;
import com.sl.mentalhealth.mapper.result.SemesterDangerCountResult;
import com.sl.mentalhealth.vo.CounselorTrendBarVO;
import com.sl.mentalhealth.vo.CounselorTrendLineVO;
import com.sl.mentalhealth.vo.CounselorTrendReportVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LocalCounselorTrendReportService {

  private final StudentAssessmentSemesterSummaryMapper studentAssessmentSemesterSummaryMapper;

  public CounselorTrendReportVO queryTrendReport(CounselorTrendReportQueryRequest request) {
    String counselorAccount = request.getCounselorAccount();
    String selectedSemester = StringUtils.hasText(request.getSemester()) ? request.getSemester() : "第1学期";

    List<ClassDangerCountResult> classRaw =
        studentAssessmentSemesterSummaryMapper.selectDangerCountByClass(counselorAccount, selectedSemester);

    List<SemesterDangerCountResult> semesterRaw =
        studentAssessmentSemesterSummaryMapper.selectDangerCountBySemester(counselorAccount);

    List<CounselorTrendBarVO> barChart = new ArrayList<>();
    for (ClassDangerCountResult item : classRaw) {
      barChart.add(new CounselorTrendBarVO(
          item.getClassName(),
          item.getDangerCount() == null ? 0L : item.getDangerCount()
      ));
    }

    List<CounselorTrendLineVO> lineChart = new ArrayList<>();
    for (SemesterDangerCountResult item : semesterRaw) {
      lineChart.add(new CounselorTrendLineVO(
          item.getSemester(),
          item.getDangerCount() == null ? 0L : item.getDangerCount()
      ));
    }

    CounselorTrendReportVO vo = new CounselorTrendReportVO();
    vo.setSemesterOptions(buildSemesterOptions());
    vo.setSelectedSemester(selectedSemester);
    vo.setBarChart(barChart);
    vo.setLineChart(lineChart);
    return vo;
  }

  private List<String> buildSemesterOptions() {
    List<String> list = new ArrayList<>();
    for (int i = 1; i <= 8; i++) {
      list.add("第" + i + "学期");
    }
    return list;
  }
}