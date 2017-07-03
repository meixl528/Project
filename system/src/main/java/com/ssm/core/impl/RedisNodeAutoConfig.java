package com.ssm.core.impl;
import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.springframework.data.redis.connection.RedisNode;

/**
 * auto create RedisNode list, using a config list.
 * <p>
 * invalid sentinel config will be ignore
 */
public class RedisNodeAutoConfig extends ArrayList<RedisNode> {

    /**
	 */
	private static final long serialVersionUID = 1L;

	public void setSentinels(String[] sentinels) {
        for (String s : sentinels) {
            if (StringUtils.isBlank(s) || s.contains("$")) {
                continue;
            }
            String[] ss = s.split(":");
            RedisNode redisNode = new RedisNode(ss[0].trim(), Integer.parseInt(ss[1].trim()));
            add(redisNode);
        }
    }
}