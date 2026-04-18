package com.sl.mentalhealth.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MybatisPlusConfig {

  /**
   * MyBatis-Plus 主拦截器
   * 先配置分页，后面你做列表分页查询时可以直接用
   */
  @Bean
  public MybatisPlusInterceptor mybatisPlusInterceptor() {
    MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

    // 分页插件，数据库类型为 MySQL
    PaginationInnerInterceptor paginationInnerInterceptor =
        new PaginationInnerInterceptor(DbType.MYSQL);

    // 超出最大页后是否回到首页，按需开启；这里先不开
    paginationInnerInterceptor.setOverflow(false);

    // 单页分页条数限制，防止有人一次查太多；按需调整
    paginationInnerInterceptor.setMaxLimit(500L);

    interceptor.addInnerInterceptor(paginationInnerInterceptor);
    return interceptor;
  }
}