package com.sl.mentalhealth.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sl.mentalhealth.dto.CounselorTrendReportQueryRequest;
import com.sl.mentalhealth.mapper.StudentAssessmentSemesterSummaryMapper;
import com.sl.mentalhealth.mapper.result.ClassDangerCountResult;
import com.sl.mentalhealth.mapper.result.SemesterDangerCountResult;
import com.sl.mentalhealth.vo.CounselorTrendBarVO;
import com.sl.mentalhealth.vo.CounselorTrendLineVO;
import com.sl.mentalhealth.vo.CounselorTrendReportVO;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class LocalCounselorTrendReportService {

  /**
   * 趋势报表缓存时间。
   * 趋势图属于聚合统计查询，数据变化频率较低，这里缓存15分钟。
   */
  private static final long TREND_REPORT_CACHE_TTL_MINUTES = 15L;

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  private final StudentAssessmentSemesterSummaryMapper studentAssessmentSemesterSummaryMapper;
  private final StringRedisTemplate stringRedisTemplate;

  public CounselorTrendReportVO queryTrendReport(CounselorTrendReportQueryRequest request) {
    if (request == null) {
      throw new IllegalArgumentException("查询参数不能为空");
    }

    String counselorAccount = request.getCounselorAccount();
    if (!StringUtils.hasText(counselorAccount)) {
      throw new IllegalArgumentException("辅导员账号不能为空");
    }

    String selectedSemester = StringUtils.hasText(request.getSemester())
        ? request.getSemester().trim()
        : "第1学期";

    String cacheKey = buildTrendReportCacheKey(counselorAccount, selectedSemester);

    CounselorTrendReportVO cachedReport = readTrendReportCache(cacheKey);
    if (cachedReport != null) {
      return cachedReport;
    }

    List<ClassDangerCountResult> classRaw =
        studentAssessmentSemesterSummaryMapper.selectDangerCountByClass(
            counselorAccount,
            selectedSemester
        );

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

    writeTrendReportCache(cacheKey, vo);

    return vo;
  }

  /**
   * 管理员修改辅导员绑定班级后，或者测评提交后需要立即刷新趋势图时，可以调用这个方法清理缓存。
   */
  public void evictTrendReportCache(String counselorAccount) {
    if (!StringUtils.hasText(counselorAccount)) {
      return;
    }

    Set<String> keys = stringRedisTemplate.keys(
        "trend:report:" + normalizeKeyPart(counselorAccount) + ":*"
    );

    if (keys != null && !keys.isEmpty()) {
      stringRedisTemplate.delete(keys);
    }
  }

  private String buildTrendReportCacheKey(String counselorAccount, String semester) {
    return "trend:report:"
        + normalizeKeyPart(counselorAccount)
        + ":"
        + normalizeKeyPart(semester);
  }

  private CounselorTrendReportVO readTrendReportCache(String cacheKey) {
    String json = stringRedisTemplate.opsForValue().get(cacheKey);
    if (!StringUtils.hasText(json)) {
      return null;
    }

    try {
      Map<String, Object> map = OBJECT_MAPPER.readValue(
          json,
          new TypeReference<Map<String, Object>>() {
          }
      );

      CounselorTrendReportVO vo = new CounselorTrendReportVO();

      vo.setSelectedSemester((String) map.get("selectedSemester"));

      List<String> semesterOptions = OBJECT_MAPPER.convertValue(
          map.get("semesterOptions"),
          new TypeReference<List<String>>() {
          }
      );
      vo.setSemesterOptions(semesterOptions == null ? buildSemesterOptions() : semesterOptions);

      List<Map<String, Object>> barRows = OBJECT_MAPPER.convertValue(
          map.get("barChart"),
          new TypeReference<List<Map<String, Object>>>() {
          }
      );

      List<CounselorTrendBarVO> barChart = new ArrayList<>();
      if (barRows != null) {
        for (Map<String, Object> row : barRows) {
          String className = row.get("className") == null ? null : row.get("className").toString();
          Long dangerCount = toLong(row.get("dangerCount"));
          barChart.add(new CounselorTrendBarVO(className, dangerCount));
        }
      }
      vo.setBarChart(barChart);

      List<Map<String, Object>> lineRows = OBJECT_MAPPER.convertValue(
          map.get("lineChart"),
          new TypeReference<List<Map<String, Object>>>() {
          }
      );

      List<CounselorTrendLineVO> lineChart = new ArrayList<>();
      if (lineRows != null) {
        for (Map<String, Object> row : lineRows) {
          String semester = row.get("semester") == null ? null : row.get("semester").toString();
          Long dangerCount = toLong(row.get("dangerCount"));
          lineChart.add(new CounselorTrendLineVO(semester, dangerCount));
        }
      }
      vo.setLineChart(lineChart);

      return vo;
    } catch (Exception e) {
      stringRedisTemplate.delete(cacheKey);
      return null;
    }
  }

  private void writeTrendReportCache(String cacheKey, CounselorTrendReportVO vo) {
    try {
      Map<String, Object> map = new HashMap<>();
      map.put("semesterOptions", vo.getSemesterOptions());
      map.put("selectedSemester", vo.getSelectedSemester());

      List<Map<String, Object>> barRows = new ArrayList<>();
      if (vo.getBarChart() != null) {
        for (CounselorTrendBarVO item : vo.getBarChart()) {
          Map<String, Object> row = new HashMap<>();
          row.put("className", item.getClassName());
          row.put("dangerCount", item.getDangerCount());
          barRows.add(row);
        }
      }
      map.put("barChart", barRows);

      List<Map<String, Object>> lineRows = new ArrayList<>();
      if (vo.getLineChart() != null) {
        for (CounselorTrendLineVO item : vo.getLineChart()) {
          Map<String, Object> row = new HashMap<>();
          row.put("semester", item.getSemester());
          row.put("dangerCount", item.getDangerCount());
          lineRows.add(row);
        }
      }
      map.put("lineChart", lineRows);

      String json = OBJECT_MAPPER.writeValueAsString(map);
      stringRedisTemplate.opsForValue().set(
          cacheKey,
          json,
          TREND_REPORT_CACHE_TTL_MINUTES,
          TimeUnit.MINUTES
      );
    } catch (Exception ignored) {
      // Redis 缓存失败不影响主业务查询
    }
  }

  private Long toLong(Object value) {
    if (value == null) {
      return 0L;
    }

    if (value instanceof Number number) {
      return number.longValue();
    }

    try {
      return Long.parseLong(value.toString());
    } catch (NumberFormatException e) {
      return 0L;
    }
  }

  private List<String> buildSemesterOptions() {
    List<String> list = new ArrayList<>();
    for (int i = 1; i <= 8; i++) {
      list.add("第" + i + "学期");
    }
    return list;
  }

  private String normalizeKeyPart(String value) {
    if (!StringUtils.hasText(value)) {
      return "unknown";
    }
    return value.trim().replace(":", "_").replace(" ", "_");
  }
}