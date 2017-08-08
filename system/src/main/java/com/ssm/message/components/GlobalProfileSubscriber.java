package com.ssm.message.components;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import com.ssm.activeMQ.annotation.Topic;
import com.ssm.activeMQ.listener.ITopicListener;
import com.ssm.core.request.IRequest;
import com.ssm.core.request.impl.RequestHelper;
import com.ssm.message.profile.GlobalProfileListener;
import com.ssm.message.profile.ListenerInitHandler;
import com.ssm.message.profile.SystemConfigListener;
import com.ssm.sys.dto.GlobalProfile;
import com.ssm.sys.service.IProfileService;
import com.ssm.sys.service.ISysConfigService;
import com.ssm.util.StringUtil;

@Component
@Topic(channel = { "profile", "config" })
public class GlobalProfileSubscriber implements ITopicListener<GlobalProfile>, ListenerInitHandler, ApplicationListener<ContextRefreshedEvent> {

    private static Map<GlobalProfileListener, List<String>> listenerMap = new HashMap<>();

    @Autowired
    private IProfileService profileService;

    @Autowired
    private ISysConfigService configService;

    private Logger logger = LoggerFactory.getLogger(GlobalProfileSubscriber.class);
    
    @Override
	public String[] getTopic() {
		return new String[]{"profile", "config"};
	}

    public void addListener(GlobalProfileListener listener) {
        listenerMap.put(listener, listener.getAcceptedProfiles());
        initLoad(listener);
    }

    @Override
    public void initLoad(GlobalProfileListener listener) {
        if (logger.isDebugEnabled()) {
            logger.debug("initial load profile values for:" + listener);
        }
        // 初始化 系统配置
        if (listener instanceof SystemConfigListener) {
            for (String configCode : listener.getAcceptedProfiles()) {
                String configValue = configService.getConfigValue(configCode);
                if (StringUtil.isNotEmpty(configValue)) {
                    listener.updateProfile(configCode, configValue);
                }
            }
        } else {
            IRequest iRequest = RequestHelper.newEmptyRequest();
            iRequest.setUserId(-1L);
            iRequest.setRoleId(-1L);
            for (String profileName : listener.getAcceptedProfiles()) {
                String profileValue = profileService.getProfileValue(iRequest, profileName);
                if (StringUtil.isNotEmpty(profileValue)) {
                    listener.updateProfile(profileName, profileValue);
                }
            }
        }
    }

    @Override
    public void onTopicMessage(GlobalProfile message, String pattern) {
        listenerMap.forEach((k, v) -> {
            // channel=config notify SystemConfig
            if ("config".equalsIgnoreCase(pattern)) {
                if (k instanceof SystemConfigListener) {
                    if (v.contains(message.getProfileName())) {
                        k.updateProfile(message.getProfileName(), message.getProfileValue());
                    }
                }
            } else if ("profile".equalsIgnoreCase(pattern)) {
                if (!(k instanceof SystemConfigListener))
                    if (v.contains(message.getProfileName())) {
                        k.updateProfile(message.getProfileName(), message.getProfileValue());
                    }
            }
        });
    }

    /**
     * find all GlobalProfileListener .
     * 
     * @throws Exception
     */
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		ApplicationContext applicationContext = event.getApplicationContext();
		Map<String, GlobalProfileListener> listeners = applicationContext.getBeansOfType(GlobalProfileListener.class);
	    listeners.forEach((k, v) -> addListener(v));
	}

}