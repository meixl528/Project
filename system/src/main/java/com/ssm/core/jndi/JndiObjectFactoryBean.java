package com.ssm.core.jndi;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.config.PlaceholderConfigurerSupport;

public class JndiObjectFactoryBean extends org.springframework.jndi.JndiObjectFactoryBean {

    @Override
    public void setJndiName(String jndiName) {
        if (jndiName.startsWith(PlaceholderConfigurerSupport.DEFAULT_PLACEHOLDER_PREFIX)) {
            String rawKey = jndiName.substring(2, jndiName.length() - 1);
            String defaultValue = null;
            int idx = rawKey.indexOf(PlaceholderConfigurerSupport.DEFAULT_VALUE_SEPARATOR);
            if (idx != -1) {
                rawKey = StringUtils.trim(rawKey.substring(0, idx));
                defaultValue = StringUtils.trim(rawKey.substring(idx + 1));
            }
            Properties properties = new Properties();
            try {
                properties.load(JndiObjectFactoryBean.class.getResourceAsStream("/config.properties"));
                jndiName = properties.getProperty(rawKey);
                if (jndiName == null && defaultValue != null) {
                    jndiName = defaultValue;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String[] jndiNames = jndiName.split(",");
        if(jndiNames.length > 1){
        	jndiName = jndiNames[0];
        }
        super.setJndiName(jndiName);
    }

}