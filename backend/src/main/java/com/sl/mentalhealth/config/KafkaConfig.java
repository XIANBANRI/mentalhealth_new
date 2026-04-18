package com.sl.mentalhealth.config;

import com.sl.mentalhealth.kafka.KafkaTopics;
import com.sl.mentalhealth.kafka.message.AppointmentRequestMessage;
import com.sl.mentalhealth.kafka.message.AppointmentResponseMessage;
import com.sl.mentalhealth.kafka.message.AssessmentRequestMessage;
import com.sl.mentalhealth.kafka.message.AssessmentResponseMessage;
import com.sl.mentalhealth.kafka.message.AssessmentScaleManageRequestMessage;
import com.sl.mentalhealth.kafka.message.AssessmentScaleManageResponseMessage;
import com.sl.mentalhealth.kafka.message.CounselorProfileRequestMessage;
import com.sl.mentalhealth.kafka.message.CounselorProfileResponseMessage;
import com.sl.mentalhealth.kafka.message.LoginRequestMessage;
import com.sl.mentalhealth.kafka.message.LoginResponseMessage;
import com.sl.mentalhealth.kafka.message.ResetPasswordRequestMessage;
import com.sl.mentalhealth.kafka.message.ResetPasswordResponseMessage;
import com.sl.mentalhealth.kafka.message.StudentProfileRequestMessage;
import com.sl.mentalhealth.kafka.message.StudentProfileResponseMessage;
import com.sl.mentalhealth.kafka.message.TeacherAppointmentRequestMessage;
import com.sl.mentalhealth.kafka.message.TeacherAppointmentResponseMessage;
import com.sl.mentalhealth.kafka.message.TeacherProfileRequestMessage;
import com.sl.mentalhealth.kafka.message.TeacherProfileResponseMessage;
import com.sl.mentalhealth.kafka.message.TeacherScheduleRequestMessage;
import com.sl.mentalhealth.kafka.message.TeacherScheduleResponseMessage;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;
import com.sl.mentalhealth.kafka.message.CounselorStudentRequestMessage;
import com.sl.mentalhealth.kafka.message.CounselorStudentResponseMessage;
import com.sl.mentalhealth.kafka.message.CounselorWarningRequestMessage;
import com.sl.mentalhealth.kafka.message.CounselorWarningResponseMessage;
import com.sl.mentalhealth.kafka.message.CounselorTrendReportRequestMessage;
import com.sl.mentalhealth.kafka.message.CounselorTrendReportResponseMessage;
import com.sl.mentalhealth.kafka.message.AdminProfileRequestMessage;
import com.sl.mentalhealth.kafka.message.AdminProfileResponseMessage;
import com.sl.mentalhealth.kafka.message.AdminTeacherManageRequestMessage;
import com.sl.mentalhealth.kafka.message.AdminTeacherManageResponseMessage;
import com.sl.mentalhealth.kafka.message.AdminCounselorManageRequestMessage;
import com.sl.mentalhealth.kafka.message.AdminCounselorManageResponseMessage;

@Configuration
public class KafkaConfig {

  private static final String BOOTSTRAP_SERVERS = "localhost:9092";

  @Bean
  public NewTopic loginRequestTopic() {
    return new NewTopic(KafkaTopics.LOGIN_REQUEST, 1, (short) 1);
  }

  @Bean
  public NewTopic loginResponseTopic() {
    return new NewTopic(KafkaTopics.LOGIN_RESPONSE, 1, (short) 1);
  }

  @Bean
  public NewTopic resetPasswordRequestTopic() {
    return new NewTopic(KafkaTopics.RESET_PASSWORD_REQUEST, 1, (short) 1);
  }

  @Bean
  public NewTopic resetPasswordResponseTopic() {
    return new NewTopic(KafkaTopics.RESET_PASSWORD_RESPONSE, 1, (short) 1);
  }

  @Bean
  public NewTopic studentProfileRequestTopic() {
    return new NewTopic(KafkaTopics.STUDENT_PROFILE_REQUEST, 1, (short) 1);
  }

  @Bean
  public NewTopic studentProfileResponseTopic() {
    return new NewTopic(KafkaTopics.STUDENT_PROFILE_RESPONSE, 1, (short) 1);
  }

  @Bean
  public NewTopic teacherProfileRequestTopic() {
    return new NewTopic(KafkaTopics.TEACHER_PROFILE_REQUEST, 1, (short) 1);
  }

  @Bean
  public NewTopic teacherProfileResponseTopic() {
    return new NewTopic(KafkaTopics.TEACHER_PROFILE_RESPONSE, 1, (short) 1);
  }

  @Bean
  public NewTopic assessmentRequestTopic() {
    return new NewTopic(KafkaTopics.ASSESSMENT_REQUEST, 1, (short) 1);
  }

  @Bean
  public NewTopic assessmentResponseTopic() {
    return new NewTopic(KafkaTopics.ASSESSMENT_RESPONSE, 1, (short) 1);
  }

  @Bean
  public NewTopic appointmentRequestTopic() {
    return new NewTopic(KafkaTopics.APPOINTMENT_REQUEST, 1, (short) 1);
  }

  @Bean
  public NewTopic appointmentResponseTopic() {
    return new NewTopic(KafkaTopics.APPOINTMENT_RESPONSE, 1, (short) 1);
  }

  @Bean
  public NewTopic teacherScheduleRequestTopic() {
    return new NewTopic(KafkaTopics.TEACHER_SCHEDULE_REQUEST, 1, (short) 1);
  }

  @Bean
  public NewTopic teacherScheduleResponseTopic() {
    return new NewTopic(KafkaTopics.TEACHER_SCHEDULE_RESPONSE, 1, (short) 1);
  }

  @Bean
  public NewTopic teacherAppointmentRequestTopic() {
    return new NewTopic(KafkaTopics.TEACHER_APPOINTMENT_REQUEST, 1, (short) 1);
  }

  @Bean
  public NewTopic teacherAppointmentResponseTopic() {
    return new NewTopic(KafkaTopics.TEACHER_APPOINTMENT_RESPONSE, 1, (short) 1);
  }

  @Bean
  public NewTopic counselorProfileRequestTopic() {
    return new NewTopic(KafkaTopics.COUNSELOR_PROFILE_REQUEST, 1, (short) 1);
  }

  @Bean
  public NewTopic counselorProfileResponseTopic() {
    return new NewTopic(KafkaTopics.COUNSELOR_PROFILE_RESPONSE, 1, (short) 1);
  }

  @Bean
  public NewTopic assessmentScaleManageRequestTopic() {
    return new NewTopic(KafkaTopics.ASSESSMENT_SCALE_MANAGE_REQUEST, 1, (short) 1);
  }

  @Bean
  public NewTopic assessmentScaleManageResponseTopic() {
    return new NewTopic(KafkaTopics.ASSESSMENT_SCALE_MANAGE_RESPONSE, 1, (short) 1);
  }

  @Bean
  public ProducerFactory<String, Object> objectProducerFactory() {
    Map<String, Object> props = new HashMap<>();
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JacksonJsonSerializer.class);
    return new DefaultKafkaProducerFactory<>(props);
  }

  @Bean
  public KafkaTemplate<String, Object> kafkaTemplate() {
    return new KafkaTemplate<>(objectProducerFactory());
  }

  @Bean
  public ProducerFactory<String, LoginRequestMessage> loginRequestProducerFactory() {
    Map<String, Object> props = new HashMap<>();
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JacksonJsonSerializer.class);
    return new DefaultKafkaProducerFactory<>(props);
  }

  @Bean
  public KafkaTemplate<String, LoginRequestMessage> loginRequestKafkaTemplate() {
    return new KafkaTemplate<>(loginRequestProducerFactory());
  }

  @Bean
  public ProducerFactory<String, LoginResponseMessage> loginResponseProducerFactory() {
    Map<String, Object> props = new HashMap<>();
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JacksonJsonSerializer.class);
    return new DefaultKafkaProducerFactory<>(props);
  }

  @Bean
  public KafkaTemplate<String, LoginResponseMessage> loginResponseKafkaTemplate() {
    return new KafkaTemplate<>(loginResponseProducerFactory());
  }

  @Bean
  public ProducerFactory<String, ResetPasswordRequestMessage> resetPasswordRequestProducerFactory() {
    Map<String, Object> props = new HashMap<>();
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JacksonJsonSerializer.class);
    return new DefaultKafkaProducerFactory<>(props);
  }

  @Bean
  public KafkaTemplate<String, ResetPasswordRequestMessage> resetPasswordRequestKafkaTemplate() {
    return new KafkaTemplate<>(resetPasswordRequestProducerFactory());
  }

  @Bean
  public ProducerFactory<String, ResetPasswordResponseMessage> resetPasswordResponseProducerFactory() {
    Map<String, Object> props = new HashMap<>();
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JacksonJsonSerializer.class);
    return new DefaultKafkaProducerFactory<>(props);
  }

  @Bean
  public KafkaTemplate<String, ResetPasswordResponseMessage> resetPasswordResponseKafkaTemplate() {
    return new KafkaTemplate<>(resetPasswordResponseProducerFactory());
  }

  @Bean
  public ProducerFactory<String, StudentProfileRequestMessage> studentProfileRequestProducerFactory() {
    Map<String, Object> props = new HashMap<>();
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JacksonJsonSerializer.class);
    return new DefaultKafkaProducerFactory<>(props);
  }

  @Bean
  public KafkaTemplate<String, StudentProfileRequestMessage> studentProfileRequestKafkaTemplate() {
    return new KafkaTemplate<>(studentProfileRequestProducerFactory());
  }

  @Bean
  public ProducerFactory<String, StudentProfileResponseMessage> studentProfileResponseProducerFactory() {
    Map<String, Object> props = new HashMap<>();
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JacksonJsonSerializer.class);
    return new DefaultKafkaProducerFactory<>(props);
  }

  @Bean
  public KafkaTemplate<String, StudentProfileResponseMessage> studentProfileResponseKafkaTemplate() {
    return new KafkaTemplate<>(studentProfileResponseProducerFactory());
  }

  @Bean
  public ProducerFactory<String, TeacherProfileRequestMessage> teacherProfileRequestProducerFactory() {
    Map<String, Object> props = new HashMap<>();
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JacksonJsonSerializer.class);
    return new DefaultKafkaProducerFactory<>(props);
  }

  @Bean
  public KafkaTemplate<String, TeacherProfileRequestMessage> teacherProfileRequestKafkaTemplate() {
    return new KafkaTemplate<>(teacherProfileRequestProducerFactory());
  }

  @Bean
  public ProducerFactory<String, TeacherProfileResponseMessage> teacherProfileResponseProducerFactory() {
    Map<String, Object> props = new HashMap<>();
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JacksonJsonSerializer.class);
    return new DefaultKafkaProducerFactory<>(props);
  }

  @Bean
  public KafkaTemplate<String, TeacherProfileResponseMessage> teacherProfileResponseKafkaTemplate() {
    return new KafkaTemplate<>(teacherProfileResponseProducerFactory());
  }

  @Bean
  public ProducerFactory<String, AssessmentRequestMessage> assessmentRequestProducerFactory() {
    Map<String, Object> props = new HashMap<>();
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JacksonJsonSerializer.class);
    return new DefaultKafkaProducerFactory<>(props);
  }

  @Bean
  public KafkaTemplate<String, AssessmentRequestMessage> assessmentRequestKafkaTemplate() {
    return new KafkaTemplate<>(assessmentRequestProducerFactory());
  }

  @Bean
  public ProducerFactory<String, AssessmentResponseMessage> assessmentResponseProducerFactory() {
    Map<String, Object> props = new HashMap<>();
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JacksonJsonSerializer.class);
    return new DefaultKafkaProducerFactory<>(props);
  }

  @Bean
  public KafkaTemplate<String, AssessmentResponseMessage> assessmentResponseKafkaTemplate() {
    return new KafkaTemplate<>(assessmentResponseProducerFactory());
  }

  @Bean
  public ProducerFactory<String, AppointmentRequestMessage> appointmentRequestProducerFactory() {
    Map<String, Object> props = new HashMap<>();
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JacksonJsonSerializer.class);
    return new DefaultKafkaProducerFactory<>(props);
  }

  @Bean
  public KafkaTemplate<String, AppointmentRequestMessage> appointmentRequestKafkaTemplate() {
    return new KafkaTemplate<>(appointmentRequestProducerFactory());
  }

  @Bean
  public ProducerFactory<String, AppointmentResponseMessage> appointmentResponseProducerFactory() {
    Map<String, Object> props = new HashMap<>();
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JacksonJsonSerializer.class);
    return new DefaultKafkaProducerFactory<>(props);
  }

  @Bean
  public KafkaTemplate<String, AppointmentResponseMessage> appointmentResponseKafkaTemplate() {
    return new KafkaTemplate<>(appointmentResponseProducerFactory());
  }

  @Bean
  public ProducerFactory<String, TeacherScheduleRequestMessage> teacherScheduleRequestProducerFactory() {
    Map<String, Object> props = new HashMap<>();
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JacksonJsonSerializer.class);
    return new DefaultKafkaProducerFactory<>(props);
  }

  @Bean
  public KafkaTemplate<String, TeacherScheduleRequestMessage> teacherScheduleRequestKafkaTemplate() {
    return new KafkaTemplate<>(teacherScheduleRequestProducerFactory());
  }

  @Bean
  public ProducerFactory<String, TeacherScheduleResponseMessage> teacherScheduleResponseProducerFactory() {
    Map<String, Object> props = new HashMap<>();
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JacksonJsonSerializer.class);
    return new DefaultKafkaProducerFactory<>(props);
  }

  @Bean
  public KafkaTemplate<String, TeacherScheduleResponseMessage> teacherScheduleResponseKafkaTemplate() {
    return new KafkaTemplate<>(teacherScheduleResponseProducerFactory());
  }

  @Bean
  public ProducerFactory<String, TeacherAppointmentRequestMessage> teacherAppointmentRequestProducerFactory() {
    Map<String, Object> props = new HashMap<>();
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JacksonJsonSerializer.class);
    return new DefaultKafkaProducerFactory<>(props);
  }

  @Bean
  public KafkaTemplate<String, TeacherAppointmentRequestMessage> teacherAppointmentRequestKafkaTemplate() {
    return new KafkaTemplate<>(teacherAppointmentRequestProducerFactory());
  }

  @Bean
  public ProducerFactory<String, TeacherAppointmentResponseMessage> teacherAppointmentResponseProducerFactory() {
    Map<String, Object> props = new HashMap<>();
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JacksonJsonSerializer.class);
    return new DefaultKafkaProducerFactory<>(props);
  }

  @Bean
  public KafkaTemplate<String, TeacherAppointmentResponseMessage> teacherAppointmentResponseKafkaTemplate() {
    return new KafkaTemplate<>(teacherAppointmentResponseProducerFactory());
  }

  @Bean
  public ProducerFactory<String, CounselorProfileRequestMessage> counselorProfileRequestProducerFactory() {
    Map<String, Object> props = new HashMap<>();
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JacksonJsonSerializer.class);
    return new DefaultKafkaProducerFactory<>(props);
  }

  @Bean
  public KafkaTemplate<String, CounselorProfileRequestMessage> counselorProfileRequestKafkaTemplate() {
    return new KafkaTemplate<>(counselorProfileRequestProducerFactory());
  }

  @Bean
  public ProducerFactory<String, CounselorProfileResponseMessage> counselorProfileResponseProducerFactory() {
    Map<String, Object> props = new HashMap<>();
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JacksonJsonSerializer.class);
    return new DefaultKafkaProducerFactory<>(props);
  }

  @Bean
  public KafkaTemplate<String, CounselorProfileResponseMessage> counselorProfileResponseKafkaTemplate() {
    return new KafkaTemplate<>(counselorProfileResponseProducerFactory());
  }

  @Bean
  public ProducerFactory<String, AssessmentScaleManageRequestMessage> assessmentScaleManageRequestProducerFactory() {
    Map<String, Object> props = new HashMap<>();
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JacksonJsonSerializer.class);
    return new DefaultKafkaProducerFactory<>(props);
  }

  @Bean
  public KafkaTemplate<String, AssessmentScaleManageRequestMessage> assessmentScaleManageRequestKafkaTemplate() {
    return new KafkaTemplate<>(assessmentScaleManageRequestProducerFactory());
  }

  @Bean
  public ProducerFactory<String, AssessmentScaleManageResponseMessage> assessmentScaleManageResponseProducerFactory() {
    Map<String, Object> props = new HashMap<>();
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JacksonJsonSerializer.class);
    return new DefaultKafkaProducerFactory<>(props);
  }

  @Bean
  public KafkaTemplate<String, AssessmentScaleManageResponseMessage> assessmentScaleManageResponseKafkaTemplate() {
    return new KafkaTemplate<>(assessmentScaleManageResponseProducerFactory());
  }

  @Bean(name = "kafkaListenerContainerFactory")
  public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory() {
    Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
    props.put(ConsumerConfig.GROUP_ID_CONFIG, "mh-default-listener-group");
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JacksonJsonDeserializer.class);

    JacksonJsonDeserializer<Object> deserializer = new JacksonJsonDeserializer<>(Object.class);
    deserializer.addTrustedPackages(
        "com.sl.mentalhealth.kafka.message",
        "com.sl.mentalhealth.dto",
        "com.sl.mentalhealth.vo",
        "java.util"
    );

    ConsumerFactory<String, Object> consumerFactory =
        new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);

    ConcurrentKafkaListenerContainerFactory<String, Object> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(consumerFactory);
    return factory;
  }

  @Bean
  public ConsumerFactory<String, LoginRequestMessage> loginRequestConsumerFactory() {
    Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
    props.put(ConsumerConfig.GROUP_ID_CONFIG, "mh-login-request-group");
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JacksonJsonDeserializer.class);

    JacksonJsonDeserializer<LoginRequestMessage> deserializer =
        new JacksonJsonDeserializer<>(LoginRequestMessage.class);
    deserializer.addTrustedPackages("com.sl.mentalhealth.kafka.message");

    return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, LoginRequestMessage>
  loginRequestKafkaListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, LoginRequestMessage> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(loginRequestConsumerFactory());
    return factory;
  }

  @Bean
  public ConsumerFactory<String, LoginResponseMessage> loginResponseConsumerFactory() {
    Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
    props.put(ConsumerConfig.GROUP_ID_CONFIG, "mh-login-response-group");
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JacksonJsonDeserializer.class);

    JacksonJsonDeserializer<LoginResponseMessage> deserializer =
        new JacksonJsonDeserializer<>(LoginResponseMessage.class);
    deserializer.addTrustedPackages("com.sl.mentalhealth.kafka.message");

    return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, LoginResponseMessage>
  loginResponseKafkaListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, LoginResponseMessage> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(loginResponseConsumerFactory());
    return factory;
  }

  @Bean
  public ConsumerFactory<String, ResetPasswordRequestMessage> resetPasswordRequestConsumerFactory() {
    Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
    props.put(ConsumerConfig.GROUP_ID_CONFIG, "mh-reset-password-request-group");
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JacksonJsonDeserializer.class);

    JacksonJsonDeserializer<ResetPasswordRequestMessage> deserializer =
        new JacksonJsonDeserializer<>(ResetPasswordRequestMessage.class);
    deserializer.addTrustedPackages("com.sl.mentalhealth.kafka.message");

    return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, ResetPasswordRequestMessage>
  resetPasswordRequestKafkaListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, ResetPasswordRequestMessage> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(resetPasswordRequestConsumerFactory());
    return factory;
  }

  @Bean
  public ConsumerFactory<String, ResetPasswordResponseMessage> resetPasswordResponseConsumerFactory() {
    Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
    props.put(ConsumerConfig.GROUP_ID_CONFIG, "mh-reset-password-response-group");
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JacksonJsonDeserializer.class);

    JacksonJsonDeserializer<ResetPasswordResponseMessage> deserializer =
        new JacksonJsonDeserializer<>(ResetPasswordResponseMessage.class);
    deserializer.addTrustedPackages("com.sl.mentalhealth.kafka.message");

    return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, ResetPasswordResponseMessage>
  resetPasswordResponseKafkaListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, ResetPasswordResponseMessage> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(resetPasswordResponseConsumerFactory());
    return factory;
  }

  @Bean
  public ConsumerFactory<String, StudentProfileRequestMessage> studentProfileRequestConsumerFactory() {
    Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
    props.put(ConsumerConfig.GROUP_ID_CONFIG, "mh-student-profile-request-group");
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JacksonJsonDeserializer.class);

    JacksonJsonDeserializer<StudentProfileRequestMessage> deserializer =
        new JacksonJsonDeserializer<>(StudentProfileRequestMessage.class);
    deserializer.addTrustedPackages("com.sl.mentalhealth.kafka.message");

    return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, StudentProfileRequestMessage>
  studentProfileRequestKafkaListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, StudentProfileRequestMessage> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(studentProfileRequestConsumerFactory());
    return factory;
  }

  @Bean
  public ConsumerFactory<String, StudentProfileResponseMessage> studentProfileResponseConsumerFactory() {
    Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
    props.put(ConsumerConfig.GROUP_ID_CONFIG, "mh-student-profile-response-group");
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JacksonJsonDeserializer.class);

    JacksonJsonDeserializer<StudentProfileResponseMessage> deserializer =
        new JacksonJsonDeserializer<>(StudentProfileResponseMessage.class);
    deserializer.addTrustedPackages("com.sl.mentalhealth.kafka.message");

    return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, StudentProfileResponseMessage>
  studentProfileResponseKafkaListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, StudentProfileResponseMessage> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(studentProfileResponseConsumerFactory());
    return factory;
  }

  @Bean
  public ConsumerFactory<String, TeacherProfileRequestMessage> teacherProfileRequestConsumerFactory() {
    Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
    props.put(ConsumerConfig.GROUP_ID_CONFIG, "mh-teacher-profile-request-group");
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JacksonJsonDeserializer.class);

    JacksonJsonDeserializer<TeacherProfileRequestMessage> deserializer =
        new JacksonJsonDeserializer<>(TeacherProfileRequestMessage.class);
    deserializer.addTrustedPackages("com.sl.mentalhealth.kafka.message");

    return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, TeacherProfileRequestMessage>
  teacherProfileRequestKafkaListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, TeacherProfileRequestMessage> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(teacherProfileRequestConsumerFactory());
    return factory;
  }

  @Bean
  public ConsumerFactory<String, TeacherProfileResponseMessage> teacherProfileResponseConsumerFactory() {
    Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
    props.put(ConsumerConfig.GROUP_ID_CONFIG, "mh-teacher-profile-response-group");
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JacksonJsonDeserializer.class);

    JacksonJsonDeserializer<TeacherProfileResponseMessage> deserializer =
        new JacksonJsonDeserializer<>(TeacherProfileResponseMessage.class);
    deserializer.addTrustedPackages(
        "com.sl.mentalhealth.kafka.message",
        "com.sl.mentalhealth.vo"
    );

    return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, TeacherProfileResponseMessage>
  teacherProfileResponseKafkaListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, TeacherProfileResponseMessage> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(teacherProfileResponseConsumerFactory());
    return factory;
  }

  @Bean
  public ConsumerFactory<String, AssessmentRequestMessage> assessmentRequestConsumerFactory() {
    Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
    props.put(ConsumerConfig.GROUP_ID_CONFIG, "mh-assessment-request-group");
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JacksonJsonDeserializer.class);

    JacksonJsonDeserializer<AssessmentRequestMessage> deserializer =
        new JacksonJsonDeserializer<>(AssessmentRequestMessage.class);
    deserializer.addTrustedPackages(
        "com.sl.mentalhealth.kafka.message",
        "com.sl.mentalhealth.dto"
    );

    return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, AssessmentRequestMessage>
  assessmentRequestKafkaListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, AssessmentRequestMessage> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(assessmentRequestConsumerFactory());
    return factory;
  }

  @Bean
  public ConsumerFactory<String, AssessmentResponseMessage> assessmentResponseConsumerFactory() {
    Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
    props.put(ConsumerConfig.GROUP_ID_CONFIG, "mh-assessment-response-group");
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JacksonJsonDeserializer.class);

    JacksonJsonDeserializer<AssessmentResponseMessage> deserializer =
        new JacksonJsonDeserializer<>(AssessmentResponseMessage.class);
    deserializer.addTrustedPackages(
        "com.sl.mentalhealth.kafka.message",
        "com.sl.mentalhealth.vo",
        "java.util"
    );

    return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, AssessmentResponseMessage>
  assessmentResponseKafkaListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, AssessmentResponseMessage> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(assessmentResponseConsumerFactory());
    return factory;
  }

  @Bean
  public ConsumerFactory<String, AppointmentRequestMessage> appointmentRequestConsumerFactory() {
    Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
    props.put(ConsumerConfig.GROUP_ID_CONFIG, "mh-appointment-request-group");
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JacksonJsonDeserializer.class);

    JacksonJsonDeserializer<AppointmentRequestMessage> deserializer =
        new JacksonJsonDeserializer<>(AppointmentRequestMessage.class);
    deserializer.addTrustedPackages("com.sl.mentalhealth.kafka.message");

    return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, AppointmentRequestMessage>
  appointmentRequestKafkaListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, AppointmentRequestMessage> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(appointmentRequestConsumerFactory());
    return factory;
  }

  @Bean
  public ConsumerFactory<String, AppointmentResponseMessage> appointmentResponseConsumerFactory() {
    Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
    props.put(ConsumerConfig.GROUP_ID_CONFIG, "mh-appointment-response-group");
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JacksonJsonDeserializer.class);

    JacksonJsonDeserializer<AppointmentResponseMessage> deserializer =
        new JacksonJsonDeserializer<>(AppointmentResponseMessage.class);
    deserializer.addTrustedPackages(
        "com.sl.mentalhealth.kafka.message",
        "com.sl.mentalhealth.vo",
        "java.util"
    );

    return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, AppointmentResponseMessage>
  appointmentResponseKafkaListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, AppointmentResponseMessage> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(appointmentResponseConsumerFactory());
    return factory;
  }

  @Bean
  public ConsumerFactory<String, TeacherScheduleRequestMessage> teacherScheduleRequestConsumerFactory() {
    Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
    props.put(ConsumerConfig.GROUP_ID_CONFIG, "mh-teacher-schedule-request-group");
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JacksonJsonDeserializer.class);

    JacksonJsonDeserializer<TeacherScheduleRequestMessage> deserializer =
        new JacksonJsonDeserializer<>(TeacherScheduleRequestMessage.class);
    deserializer.addTrustedPackages(
        "com.sl.mentalhealth.kafka.message",
        "com.sl.mentalhealth.dto"
    );

    return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, TeacherScheduleRequestMessage>
  teacherScheduleRequestKafkaListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, TeacherScheduleRequestMessage> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(teacherScheduleRequestConsumerFactory());
    return factory;
  }

  @Bean
  public ConsumerFactory<String, TeacherScheduleResponseMessage> teacherScheduleResponseConsumerFactory() {
    Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
    props.put(ConsumerConfig.GROUP_ID_CONFIG, "mh-teacher-schedule-response-group");
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JacksonJsonDeserializer.class);

    JacksonJsonDeserializer<TeacherScheduleResponseMessage> deserializer =
        new JacksonJsonDeserializer<>(TeacherScheduleResponseMessage.class);
    deserializer.addTrustedPackages(
        "com.sl.mentalhealth.kafka.message",
        "com.sl.mentalhealth.vo",
        "java.util"
    );

    return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, TeacherScheduleResponseMessage>
  teacherScheduleResponseKafkaListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, TeacherScheduleResponseMessage> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(teacherScheduleResponseConsumerFactory());
    return factory;
  }

  @Bean
  public ConsumerFactory<String, TeacherAppointmentRequestMessage> teacherAppointmentRequestConsumerFactory() {
    Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
    props.put(ConsumerConfig.GROUP_ID_CONFIG, "mh-teacher-appointment-request-group");
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JacksonJsonDeserializer.class);

    JacksonJsonDeserializer<TeacherAppointmentRequestMessage> deserializer =
        new JacksonJsonDeserializer<>(TeacherAppointmentRequestMessage.class);
    deserializer.addTrustedPackages(
        "com.sl.mentalhealth.kafka.message",
        "com.sl.mentalhealth.dto"
    );

    return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, TeacherAppointmentRequestMessage>
  teacherAppointmentRequestKafkaListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, TeacherAppointmentRequestMessage> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(teacherAppointmentRequestConsumerFactory());
    return factory;
  }

  @Bean
  public ConsumerFactory<String, TeacherAppointmentResponseMessage> teacherAppointmentResponseConsumerFactory() {
    Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
    props.put(ConsumerConfig.GROUP_ID_CONFIG, "mh-teacher-appointment-response-group");
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JacksonJsonDeserializer.class);

    JacksonJsonDeserializer<TeacherAppointmentResponseMessage> deserializer =
        new JacksonJsonDeserializer<>(TeacherAppointmentResponseMessage.class);
    deserializer.addTrustedPackages(
        "com.sl.mentalhealth.kafka.message",
        "com.sl.mentalhealth.vo",
        "java.util"
    );

    return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, TeacherAppointmentResponseMessage>
  teacherAppointmentResponseKafkaListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, TeacherAppointmentResponseMessage> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(teacherAppointmentResponseConsumerFactory());
    return factory;
  }

  @Bean
  public ConsumerFactory<String, CounselorProfileRequestMessage> counselorProfileRequestConsumerFactory() {
    Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
    props.put(ConsumerConfig.GROUP_ID_CONFIG, "mh-counselor-profile-request-group");
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JacksonJsonDeserializer.class);

    JacksonJsonDeserializer<CounselorProfileRequestMessage> deserializer =
        new JacksonJsonDeserializer<>(CounselorProfileRequestMessage.class);
    deserializer.addTrustedPackages("com.sl.mentalhealth.kafka.message");

    return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, CounselorProfileRequestMessage>
  counselorProfileRequestKafkaListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, CounselorProfileRequestMessage> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(counselorProfileRequestConsumerFactory());
    return factory;
  }

  @Bean
  public ConsumerFactory<String, CounselorProfileResponseMessage> counselorProfileResponseConsumerFactory() {
    Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
    props.put(ConsumerConfig.GROUP_ID_CONFIG, "mh-counselor-profile-response-group");
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JacksonJsonDeserializer.class);

    JacksonJsonDeserializer<CounselorProfileResponseMessage> deserializer =
        new JacksonJsonDeserializer<>(CounselorProfileResponseMessage.class);
    deserializer.addTrustedPackages(
        "com.sl.mentalhealth.kafka.message",
        "com.sl.mentalhealth.vo"
    );

    return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, CounselorProfileResponseMessage>
  counselorProfileResponseKafkaListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, CounselorProfileResponseMessage> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(counselorProfileResponseConsumerFactory());
    return factory;
  }

  @Bean
  public ConsumerFactory<String, AssessmentScaleManageRequestMessage> assessmentScaleManageRequestConsumerFactory() {
    Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
    props.put(ConsumerConfig.GROUP_ID_CONFIG, "mh-assessment-scale-manage-request-group");
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JacksonJsonDeserializer.class);

    JacksonJsonDeserializer<AssessmentScaleManageRequestMessage> deserializer =
        new JacksonJsonDeserializer<>(AssessmentScaleManageRequestMessage.class);
    deserializer.addTrustedPackages(
        "com.sl.mentalhealth.kafka.message",
        "com.sl.mentalhealth.dto",
        "java.util"
    );

    return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, AssessmentScaleManageRequestMessage>
  assessmentScaleManageRequestKafkaListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, AssessmentScaleManageRequestMessage> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(assessmentScaleManageRequestConsumerFactory());
    return factory;
  }

  @Bean
  public ConsumerFactory<String, AssessmentScaleManageResponseMessage> assessmentScaleManageResponseConsumerFactory() {
    Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
    props.put(ConsumerConfig.GROUP_ID_CONFIG, "mh-assessment-scale-manage-response-group");
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JacksonJsonDeserializer.class);

    JacksonJsonDeserializer<AssessmentScaleManageResponseMessage> deserializer =
        new JacksonJsonDeserializer<>(AssessmentScaleManageResponseMessage.class);
    deserializer.addTrustedPackages(
        "com.sl.mentalhealth.kafka.message",
        "com.sl.mentalhealth.dto",
        "com.sl.mentalhealth.vo",
        "java.util"
    );

    return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, AssessmentScaleManageResponseMessage>
  assessmentScaleManageResponseKafkaListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, AssessmentScaleManageResponseMessage> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(assessmentScaleManageResponseConsumerFactory());
    return factory;
  }

  @Bean
  public NewTopic counselorStudentRequestTopic() {
    return new NewTopic(KafkaTopics.COUNSELOR_STUDENT_REQUEST, 1, (short) 1);
  }

  @Bean
  public NewTopic counselorStudentResponseTopic() {
    return new NewTopic(KafkaTopics.COUNSELOR_STUDENT_RESPONSE, 1, (short) 1);
  }

  @Bean
  public ProducerFactory<String, CounselorStudentRequestMessage> counselorStudentRequestProducerFactory() {
    Map<String, Object> props = new HashMap<>();
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JacksonJsonSerializer.class);
    return new DefaultKafkaProducerFactory<>(props);
  }

  @Bean(name = "counselorStudentRequestKafkaTemplate")
  public KafkaTemplate<String, CounselorStudentRequestMessage> counselorStudentRequestKafkaTemplate() {
    return new KafkaTemplate<>(counselorStudentRequestProducerFactory());
  }

  @Bean
  public ProducerFactory<String, CounselorStudentResponseMessage> counselorStudentResponseProducerFactory() {
    Map<String, Object> props = new HashMap<>();
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JacksonJsonSerializer.class);
    return new DefaultKafkaProducerFactory<>(props);
  }

  @Bean(name = "counselorStudentResponseKafkaTemplate")
  public KafkaTemplate<String, CounselorStudentResponseMessage> counselorStudentResponseKafkaTemplate() {
    return new KafkaTemplate<>(counselorStudentResponseProducerFactory());
  }

  @Bean
  public ConsumerFactory<String, CounselorStudentRequestMessage> counselorStudentRequestConsumerFactory() {
    Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
    props.put(ConsumerConfig.GROUP_ID_CONFIG, "mh-counselor-student-request-group");
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JacksonJsonDeserializer.class);

    JacksonJsonDeserializer<CounselorStudentRequestMessage> deserializer =
        new JacksonJsonDeserializer<>(CounselorStudentRequestMessage.class);
    deserializer.addTrustedPackages("com.sl.mentalhealth.kafka.message");

    return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
  }

  @Bean(name = "counselorStudentRequestKafkaListenerContainerFactory")
  public ConcurrentKafkaListenerContainerFactory<String, CounselorStudentRequestMessage>
  counselorStudentRequestKafkaListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, CounselorStudentRequestMessage> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(counselorStudentRequestConsumerFactory());
    return factory;
  }

  @Bean
  public ConsumerFactory<String, CounselorStudentResponseMessage> counselorStudentResponseConsumerFactory() {
    Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
    props.put(ConsumerConfig.GROUP_ID_CONFIG, "mh-counselor-student-response-group");
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JacksonJsonDeserializer.class);

    JacksonJsonDeserializer<CounselorStudentResponseMessage> deserializer =
        new JacksonJsonDeserializer<>(CounselorStudentResponseMessage.class);
    deserializer.addTrustedPackages(
        "com.sl.mentalhealth.kafka.message",
        "com.sl.mentalhealth.vo",
        "java.util"
    );

    return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
  }

  @Bean(name = "counselorStudentResponseKafkaListenerContainerFactory")
  public ConcurrentKafkaListenerContainerFactory<String, CounselorStudentResponseMessage>
  counselorStudentResponseKafkaListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, CounselorStudentResponseMessage> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(counselorStudentResponseConsumerFactory());
    return factory;
  }

  @Bean
  public NewTopic counselorWarningRequestTopic() {
    return new NewTopic(KafkaTopics.COUNSELOR_WARNING_REQUEST, 1, (short) 1);
  }

  @Bean
  public NewTopic counselorWarningResponseTopic() {
    return new NewTopic(KafkaTopics.COUNSELOR_WARNING_RESPONSE, 1, (short) 1);
  }

  @Bean
  public ProducerFactory<String, CounselorWarningRequestMessage> counselorWarningRequestProducerFactory() {
    Map<String, Object> props = new HashMap<>();
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JacksonJsonSerializer.class);
    return new DefaultKafkaProducerFactory<>(props);
  }

  @Bean(name = "counselorWarningRequestKafkaTemplate")
  public KafkaTemplate<String, CounselorWarningRequestMessage> counselorWarningRequestKafkaTemplate() {
    return new KafkaTemplate<>(counselorWarningRequestProducerFactory());
  }

  @Bean
  public ProducerFactory<String, CounselorWarningResponseMessage> counselorWarningResponseProducerFactory() {
    Map<String, Object> props = new HashMap<>();
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JacksonJsonSerializer.class);
    return new DefaultKafkaProducerFactory<>(props);
  }

  @Bean(name = "counselorWarningResponseKafkaTemplate")
  public KafkaTemplate<String, CounselorWarningResponseMessage> counselorWarningResponseKafkaTemplate() {
    return new KafkaTemplate<>(counselorWarningResponseProducerFactory());
  }

  @Bean
  public ConsumerFactory<String, CounselorWarningRequestMessage> counselorWarningRequestConsumerFactory() {
    Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
    props.put(ConsumerConfig.GROUP_ID_CONFIG, "mh-counselor-warning-request-group");
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JacksonJsonDeserializer.class);

    JacksonJsonDeserializer<CounselorWarningRequestMessage> deserializer =
        new JacksonJsonDeserializer<>(CounselorWarningRequestMessage.class);
    deserializer.addTrustedPackages("com.sl.mentalhealth.kafka.message");

    return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
  }

  @Bean(name = "counselorWarningRequestKafkaListenerContainerFactory")
  public ConcurrentKafkaListenerContainerFactory<String, CounselorWarningRequestMessage>
  counselorWarningRequestKafkaListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, CounselorWarningRequestMessage> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(counselorWarningRequestConsumerFactory());
    return factory;
  }

  @Bean
  public ConsumerFactory<String, CounselorWarningResponseMessage> counselorWarningResponseConsumerFactory() {
    Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
    props.put(ConsumerConfig.GROUP_ID_CONFIG, "mh-counselor-warning-response-group");
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JacksonJsonDeserializer.class);

    JacksonJsonDeserializer<CounselorWarningResponseMessage> deserializer =
        new JacksonJsonDeserializer<>(CounselorWarningResponseMessage.class);
    deserializer.addTrustedPackages(
        "com.sl.mentalhealth.kafka.message",
        "com.sl.mentalhealth.vo",
        "java.util"
    );

    return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
  }

  @Bean(name = "counselorWarningResponseKafkaListenerContainerFactory")
  public ConcurrentKafkaListenerContainerFactory<String, CounselorWarningResponseMessage>
  counselorWarningResponseKafkaListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, CounselorWarningResponseMessage> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(counselorWarningResponseConsumerFactory());
    return factory;
  }

  @Bean
  public NewTopic counselorTrendReportRequestTopic() {
    return new NewTopic(KafkaTopics.COUNSELOR_TREND_REPORT_REQUEST, 1, (short) 1);
  }

  @Bean
  public NewTopic counselorTrendReportResponseTopic() {
    return new NewTopic(KafkaTopics.COUNSELOR_TREND_REPORT_RESPONSE, 1, (short) 1);
  }

  @Bean
  public ProducerFactory<String, CounselorTrendReportRequestMessage> counselorTrendReportRequestProducerFactory() {
    Map<String, Object> props = new HashMap<>();
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JacksonJsonSerializer.class);
    return new DefaultKafkaProducerFactory<>(props);
  }

  @Bean(name = "counselorTrendReportRequestKafkaTemplate")
  public KafkaTemplate<String, CounselorTrendReportRequestMessage> counselorTrendReportRequestKafkaTemplate() {
    return new KafkaTemplate<>(counselorTrendReportRequestProducerFactory());
  }

  @Bean
  public ProducerFactory<String, CounselorTrendReportResponseMessage> counselorTrendReportResponseProducerFactory() {
    Map<String, Object> props = new HashMap<>();
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JacksonJsonSerializer.class);
    return new DefaultKafkaProducerFactory<>(props);
  }

  @Bean(name = "counselorTrendReportResponseKafkaTemplate")
  public KafkaTemplate<String, CounselorTrendReportResponseMessage> counselorTrendReportResponseKafkaTemplate() {
    return new KafkaTemplate<>(counselorTrendReportResponseProducerFactory());
  }

  @Bean
  public ConsumerFactory<String, CounselorTrendReportRequestMessage> counselorTrendReportRequestConsumerFactory() {
    Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
    props.put(ConsumerConfig.GROUP_ID_CONFIG, "mh-counselor-trend-report-request-group");
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JacksonJsonDeserializer.class);

    JacksonJsonDeserializer<CounselorTrendReportRequestMessage> deserializer =
        new JacksonJsonDeserializer<>(CounselorTrendReportRequestMessage.class);
    deserializer.addTrustedPackages("com.sl.mentalhealth.kafka.message");

    return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
  }

  @Bean(name = "counselorTrendReportRequestKafkaListenerContainerFactory")
  public ConcurrentKafkaListenerContainerFactory<String, CounselorTrendReportRequestMessage>
  counselorTrendReportRequestKafkaListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, CounselorTrendReportRequestMessage> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(counselorTrendReportRequestConsumerFactory());
    return factory;
  }

  @Bean
  public ConsumerFactory<String, CounselorTrendReportResponseMessage> counselorTrendReportResponseConsumerFactory() {
    Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
    props.put(ConsumerConfig.GROUP_ID_CONFIG, "mh-counselor-trend-report-response-group");
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JacksonJsonDeserializer.class);

    JacksonJsonDeserializer<CounselorTrendReportResponseMessage> deserializer =
        new JacksonJsonDeserializer<>(CounselorTrendReportResponseMessage.class);
    deserializer.addTrustedPackages(
        "com.sl.mentalhealth.kafka.message",
        "com.sl.mentalhealth.vo",
        "java.util"
    );

    return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
  }

  @Bean(name = "counselorTrendReportResponseKafkaListenerContainerFactory")
  public ConcurrentKafkaListenerContainerFactory<String, CounselorTrendReportResponseMessage>
  counselorTrendReportResponseKafkaListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, CounselorTrendReportResponseMessage> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(counselorTrendReportResponseConsumerFactory());
    return factory;
  }

  @Bean
  public NewTopic adminProfileRequestTopic() {
    return new NewTopic(KafkaTopics.ADMIN_PROFILE_REQUEST, 1, (short) 1);
  }

  @Bean
  public NewTopic adminProfileResponseTopic() {
    return new NewTopic(KafkaTopics.ADMIN_PROFILE_RESPONSE, 1, (short) 1);
  }

  @Bean
  public ProducerFactory<String, AdminProfileRequestMessage> adminProfileRequestProducerFactory() {
    Map<String, Object> props = new HashMap<>();
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JacksonJsonSerializer.class);
    return new DefaultKafkaProducerFactory<>(props);
  }

  @Bean
  public KafkaTemplate<String, AdminProfileRequestMessage> adminProfileRequestKafkaTemplate() {
    return new KafkaTemplate<>(adminProfileRequestProducerFactory());
  }

  @Bean
  public ProducerFactory<String, AdminProfileResponseMessage> adminProfileResponseProducerFactory() {
    Map<String, Object> props = new HashMap<>();
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JacksonJsonSerializer.class);
    return new DefaultKafkaProducerFactory<>(props);
  }

  @Bean
  public KafkaTemplate<String, AdminProfileResponseMessage> adminProfileResponseKafkaTemplate() {
    return new KafkaTemplate<>(adminProfileResponseProducerFactory());
  }

  @Bean
  public ConsumerFactory<String, AdminProfileRequestMessage> adminProfileRequestConsumerFactory() {
    Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
    props.put(ConsumerConfig.GROUP_ID_CONFIG, "mh-admin-profile-request-group");
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JacksonJsonDeserializer.class);

    JacksonJsonDeserializer<AdminProfileRequestMessage> deserializer =
        new JacksonJsonDeserializer<>(AdminProfileRequestMessage.class);
    deserializer.addTrustedPackages("com.sl.mentalhealth.kafka.message");

    return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, AdminProfileRequestMessage>
  adminProfileRequestKafkaListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, AdminProfileRequestMessage> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(adminProfileRequestConsumerFactory());
    return factory;
  }

  @Bean
  public ConsumerFactory<String, AdminProfileResponseMessage> adminProfileResponseConsumerFactory() {
    Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
    props.put(ConsumerConfig.GROUP_ID_CONFIG, "mh-admin-profile-response-group");
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JacksonJsonDeserializer.class);

    JacksonJsonDeserializer<AdminProfileResponseMessage> deserializer =
        new JacksonJsonDeserializer<>(AdminProfileResponseMessage.class);
    deserializer.addTrustedPackages(
        "com.sl.mentalhealth.kafka.message",
        "com.sl.mentalhealth.vo"
    );

    return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, AdminProfileResponseMessage>
  adminProfileResponseKafkaListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, AdminProfileResponseMessage> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(adminProfileResponseConsumerFactory());
    return factory;
  }

  @Bean
  public NewTopic adminTeacherManageRequestTopic() {
    return new NewTopic(KafkaTopics.ADMIN_TEACHER_MANAGE_REQUEST, 1, (short) 1);
  }

  @Bean
  public NewTopic adminTeacherManageResponseTopic() {
    return new NewTopic(KafkaTopics.ADMIN_TEACHER_MANAGE_RESPONSE, 1, (short) 1);
  }

  @Bean
  public ProducerFactory<String, AdminTeacherManageRequestMessage> adminTeacherManageRequestProducerFactory() {
    Map<String, Object> props = new HashMap<>();
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JacksonJsonSerializer.class);
    return new DefaultKafkaProducerFactory<>(props);
  }

  @Bean(name = "adminTeacherManageRequestKafkaTemplate")
  public KafkaTemplate<String, AdminTeacherManageRequestMessage> adminTeacherManageRequestKafkaTemplate() {
    return new KafkaTemplate<>(adminTeacherManageRequestProducerFactory());
  }

  @Bean
  public ProducerFactory<String, AdminTeacherManageResponseMessage> adminTeacherManageResponseProducerFactory() {
    Map<String, Object> props = new HashMap<>();
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JacksonJsonSerializer.class);
    return new DefaultKafkaProducerFactory<>(props);
  }

  @Bean(name = "adminTeacherManageResponseKafkaTemplate")
  public KafkaTemplate<String, AdminTeacherManageResponseMessage> adminTeacherManageResponseKafkaTemplate() {
    return new KafkaTemplate<>(adminTeacherManageResponseProducerFactory());
  }

  @Bean
  public ConsumerFactory<String, AdminTeacherManageRequestMessage> adminTeacherManageRequestConsumerFactory() {
    Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
    props.put(ConsumerConfig.GROUP_ID_CONFIG, "mh-admin-teacher-manage-request-group");
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JacksonJsonDeserializer.class);

    JacksonJsonDeserializer<AdminTeacherManageRequestMessage> deserializer =
        new JacksonJsonDeserializer<>(AdminTeacherManageRequestMessage.class);
    deserializer.addTrustedPackages(
        "com.sl.mentalhealth.kafka.message",
        "com.sl.mentalhealth.dto",
        "java.util"
    );

    return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
  }

  @Bean(name = "adminTeacherManageRequestKafkaListenerContainerFactory")
  public ConcurrentKafkaListenerContainerFactory<String, AdminTeacherManageRequestMessage>
  adminTeacherManageRequestKafkaListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, AdminTeacherManageRequestMessage> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(adminTeacherManageRequestConsumerFactory());
    return factory;
  }

  @Bean
  public ConsumerFactory<String, AdminTeacherManageResponseMessage> adminTeacherManageResponseConsumerFactory() {
    Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
    props.put(ConsumerConfig.GROUP_ID_CONFIG, "mh-admin-teacher-manage-response-group");
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JacksonJsonDeserializer.class);

    JacksonJsonDeserializer<AdminTeacherManageResponseMessage> deserializer =
        new JacksonJsonDeserializer<>(AdminTeacherManageResponseMessage.class);
    deserializer.addTrustedPackages(
        "com.sl.mentalhealth.kafka.message",
        "com.sl.mentalhealth.vo",
        "java.util"
    );

    return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
  }

  @Bean(name = "adminTeacherManageResponseKafkaListenerContainerFactory")
  public ConcurrentKafkaListenerContainerFactory<String, AdminTeacherManageResponseMessage>
  adminTeacherManageResponseKafkaListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, AdminTeacherManageResponseMessage> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(adminTeacherManageResponseConsumerFactory());
    return factory;
  }

  @Bean
  public NewTopic adminCounselorManageRequestTopic() {
    return new NewTopic(KafkaTopics.ADMIN_COUNSELOR_MANAGE_REQUEST, 1, (short) 1);
  }

  @Bean
  public NewTopic adminCounselorManageResponseTopic() {
    return new NewTopic(KafkaTopics.ADMIN_COUNSELOR_MANAGE_RESPONSE, 1, (short) 1);
  }

  @Bean
  public ProducerFactory<String, AdminCounselorManageRequestMessage> adminCounselorManageRequestProducerFactory() {
    Map<String, Object> props = new HashMap<>();
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JacksonJsonSerializer.class);
    return new DefaultKafkaProducerFactory<>(props);
  }

  @Bean(name = "adminCounselorManageRequestKafkaTemplate")
  public KafkaTemplate<String, AdminCounselorManageRequestMessage> adminCounselorManageRequestKafkaTemplate() {
    return new KafkaTemplate<>(adminCounselorManageRequestProducerFactory());
  }

  @Bean
  public ProducerFactory<String, AdminCounselorManageResponseMessage> adminCounselorManageResponseProducerFactory() {
    Map<String, Object> props = new HashMap<>();
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JacksonJsonSerializer.class);
    return new DefaultKafkaProducerFactory<>(props);
  }

  @Bean(name = "adminCounselorManageResponseKafkaTemplate")
  public KafkaTemplate<String, AdminCounselorManageResponseMessage> adminCounselorManageResponseKafkaTemplate() {
    return new KafkaTemplate<>(adminCounselorManageResponseProducerFactory());
  }

  @Bean
  public ConsumerFactory<String, AdminCounselorManageRequestMessage> adminCounselorManageRequestConsumerFactory() {
    Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
    props.put(ConsumerConfig.GROUP_ID_CONFIG, "mh-admin-counselor-manage-request-group");
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JacksonJsonDeserializer.class);

    JacksonJsonDeserializer<AdminCounselorManageRequestMessage> deserializer =
        new JacksonJsonDeserializer<>(AdminCounselorManageRequestMessage.class);
    deserializer.addTrustedPackages(
        "com.sl.mentalhealth.kafka.message",
        "com.sl.mentalhealth.dto",
        "java.util"
    );

    return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
  }

  @Bean(name = "adminCounselorManageRequestKafkaListenerContainerFactory")
  public ConcurrentKafkaListenerContainerFactory<String, AdminCounselorManageRequestMessage>
  adminCounselorManageRequestKafkaListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, AdminCounselorManageRequestMessage> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(adminCounselorManageRequestConsumerFactory());
    return factory;
  }

  @Bean
  public ConsumerFactory<String, AdminCounselorManageResponseMessage> adminCounselorManageResponseConsumerFactory() {
    Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
    props.put(ConsumerConfig.GROUP_ID_CONFIG, "mh-admin-counselor-manage-response-group");
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JacksonJsonDeserializer.class);

    JacksonJsonDeserializer<AdminCounselorManageResponseMessage> deserializer =
        new JacksonJsonDeserializer<>(AdminCounselorManageResponseMessage.class);
    deserializer.addTrustedPackages(
        "com.sl.mentalhealth.kafka.message",
        "com.sl.mentalhealth.vo",
        "java.util"
    );

    return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
  }

  @Bean(name = "adminCounselorManageResponseKafkaListenerContainerFactory")
  public ConcurrentKafkaListenerContainerFactory<String, AdminCounselorManageResponseMessage>
  adminCounselorManageResponseKafkaListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, AdminCounselorManageResponseMessage> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(adminCounselorManageResponseConsumerFactory());
    return factory;
  }
}