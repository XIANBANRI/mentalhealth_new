package com.sl.mentalhealth.kafka;

public final class KafkaTopics {

  private KafkaTopics() {
  }

  public static final String LOGIN_REQUEST = "mh.login.request";
  public static final String LOGIN_RESPONSE = "mh.login.response";

  public static final String RESET_PASSWORD_REQUEST = "mh.reset-password.request";
  public static final String RESET_PASSWORD_RESPONSE = "mh.reset-password.response";

  public static final String STUDENT_PROFILE_REQUEST = "mh.student.profile.request";
  public static final String STUDENT_PROFILE_RESPONSE = "mh.student.profile.response";

  public static final String ASSESSMENT_REQUEST = "mh.assessment.request";
  public static final String ASSESSMENT_RESPONSE = "mh.assessment.response";

  public static final String APPOINTMENT_REQUEST = "appointment-request";
  public static final String APPOINTMENT_RESPONSE = "appointment-response";

  public static final String TEACHER_PROFILE_REQUEST = "mh.teacher.profile.request";
  public static final String TEACHER_PROFILE_RESPONSE = "mh.teacher.profile.response";

  public static final String TEACHER_SCHEDULE_REQUEST = "mh.teacher.schedule.request";
  public static final String TEACHER_SCHEDULE_RESPONSE = "mh.teacher.schedule.response";

  public static final String TEACHER_APPOINTMENT_REQUEST = "mh.teacher.appointment.request";
  public static final String TEACHER_APPOINTMENT_RESPONSE = "mh.teacher.appointment.response";

  public static final String COUNSELOR_PROFILE_REQUEST = "mh.counselor.profile.request";
  public static final String COUNSELOR_PROFILE_RESPONSE = "mh.counselor.profile.response";

  public static final String ASSESSMENT_SCALE_MANAGE_REQUEST = "mh.assessment-scale.manage.request";
  public static final String ASSESSMENT_SCALE_MANAGE_RESPONSE = "mh.assessment-scale.manage.response";

  public static final String COUNSELOR_STUDENT_REQUEST = "mh.counselor.student.request";
  public static final String COUNSELOR_STUDENT_RESPONSE = "mh.counselor.student.response";

  public static final String COUNSELOR_WARNING_REQUEST = "mh.counselor.warning.request";
  public static final String COUNSELOR_WARNING_RESPONSE = "mh.counselor.warning.response";

  public static final String COUNSELOR_TREND_REPORT_REQUEST = "counselor-trend-report-request";
  public static final String COUNSELOR_TREND_REPORT_RESPONSE = "counselor-trend-report-response";

  public static final String ADMIN_PROFILE_REQUEST = "admin-profile-request";
  public static final String ADMIN_PROFILE_RESPONSE = "admin-profile-response";

  public static final String ADMIN_TEACHER_MANAGE_REQUEST = "admin.teacher.manage.request";
  public static final String ADMIN_TEACHER_MANAGE_RESPONSE = "admin.teacher.manage.response";

  public static final String ADMIN_COUNSELOR_MANAGE_REQUEST = "admin.counselor.manage.request";
  public static final String ADMIN_COUNSELOR_MANAGE_RESPONSE = "admin.counselor.manage.response";

}