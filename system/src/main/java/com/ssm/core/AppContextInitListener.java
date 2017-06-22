package com.ssm.core;

import org.springframework.context.ApplicationContext;

/**
 * @author shengyang.zhou@hand-china.com
 */
public interface AppContextInitListener {
    void contextInitialized(ApplicationContext applicationContext);
}