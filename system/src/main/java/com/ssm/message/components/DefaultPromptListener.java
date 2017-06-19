package com.ssm.message.components;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ssm.activeMQ.annotation.Topic;
import com.ssm.activeMQ.listener.ITopicListener;
import com.ssm.sys.dto.Prompt;
import com.ssm.sys.mapper.PromptMapper;

/**
 * @author meixl
 */
@Component
@Topic(channel = "cache.prompt")
public class DefaultPromptListener implements ITopicListener<String>, InitializingBean {

    @Autowired
    private PromptMapper promptMapper;

    private Map<String, List<Prompt>> promptsMap = new HashMap<>();

    @Override
    public void onTopicMessage(String message, String pattern) {
        if (message.toLowerCase().startsWith("hap.")) {
            reload();
        }
    }

    private void reload() {
        Prompt p = new Prompt();
        p.setPromptCode("hap.");
        List<Prompt> prompts = promptMapper.select(p);
        Map<String, List<Prompt>> promptsMapNew = new HashMap<>();
        for (Prompt prompt : prompts) {
            List<Prompt> list = promptsMapNew.get(prompt.getLang());
            if (list == null) {
                list = new ArrayList<>();
                promptsMapNew.put(prompt.getLang(), list);
            }
            list.add(prompt);
        }
        promptsMap = promptsMapNew;
    }

    public List<Prompt> getDefaultPrompt(String lang) {
        return promptsMap.get(lang);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        reload();
    }

	@Override
	public String[] getTopic() {
		return null;
	}
}