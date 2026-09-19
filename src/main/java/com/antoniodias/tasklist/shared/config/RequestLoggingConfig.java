package com.antoniodias.tasklist.shared.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

@Configuration
public class RequestLoggingConfig {

    // Logs "method uri?query" for every request at DEBUG on this filter's logger
    @Bean
    public CommonsRequestLoggingFilter requestLoggingFilter() {
        CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter();
        filter.setIncludeQueryString(true);
        filter.setIncludeClientInfo(false);
        filter.setIncludeHeaders(false);
        filter.setIncludePayload(false);
        filter.setBeforeMessagePrefix("");
        filter.setBeforeMessageSuffix("");
        filter.setAfterMessagePrefix("");
        filter.setAfterMessageSuffix(" [done]");
        return filter;
    }
}
