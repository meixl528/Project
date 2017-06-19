package com.ssm.cache.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ssm.function.dto.RoleResourceItem;
import com.ssm.function.mapper.RoleResourceItemMapper;

public class RoleResourceItemCache extends HashStringRedisCache<Long[]> {
    private String roleResourceItemQueryAllSqlId = RoleResourceItemMapper.class.getName() + ".selectForCache";
    private String roleResourceItemQuerySqlId = RoleResourceItemMapper.class.getName() + ".selectResourceItemsByRole";

    private final Logger logger = LoggerFactory.getLogger(RoleResourceItemCache.class);

    {
        setType(Long[].class);
    }

    @Override
    public void init() {
        strSerializer = getRedisTemplate().getStringSerializer();
        initLoad();
    }

    /**
     *
     * @param key roleId
     *
     * @return values resourceItemIds
     */
    @Override
    public Long[] getValue(String key) {
        return super.getValue(key);
    }

    /**
     *
     * @param key roleId
     *
     * @param values values resourceItemIds
     */
    @Override
    public void setValue(String key, Long[] values) {
        super.setValue(key, values);
    }

    @SuppressWarnings("unchecked")
    protected void initLoad() {
        Map<String, List<Long>> roleResourceItems = new HashMap<>();
        try (SqlSession sqlSession = getSqlSessionFactory().openSession()) {
            sqlSession.select(roleResourceItemQueryAllSqlId, (resultContext) -> {
                RoleResourceItem value = (RoleResourceItem) resultContext.getResultObject();
                String roleId = value.getRoleId().toString();
                List<Long> resourceItems = roleResourceItems.get(roleId);
                if (resourceItems == null) {
                    resourceItems = new ArrayList<>();
                    roleResourceItems.put(roleId, resourceItems);
                }
                resourceItems.add(value.getResourceItemId());
            });

            roleResourceItems.forEach((k, v) -> {
                setValue(k, v.toArray(new Long[v.size()]));
            });
            if(logger.isDebugEnabled()){
                logger.debug("successfully loaded all role resource item cache");
            }
        } catch (Throwable e) {
            if (logger.isErrorEnabled()) {
                logger.error("init role resource item cache exception: ", e);
            }
        }
    }

    public void load(String roleId){
        Map<String, List<Long>> roleResourceItems = new HashMap<>();
        super.remove(roleId);
        try (SqlSession sqlSession = getSqlSessionFactory().openSession()) {
            sqlSession.select(roleResourceItemQuerySqlId, Long.parseLong(roleId), (resultContext) -> {
                RoleResourceItem value = (RoleResourceItem) resultContext.getResultObject();
                List<Long> sets = roleResourceItems.get(roleId);
                if (sets == null) {
                    sets = new ArrayList<>();
                    roleResourceItems.put(roleId, sets);
                }
                sets.add(value.getResourceItemId());
            });

            roleResourceItems.forEach((k, v) -> {
                setValue(k, v.toArray(new Long[v.size()]));
            });
        } catch (Throwable e) {
            if (logger.isErrorEnabled()) {
                logger.error("load role resource item cache exception: ", e);
            }
        }
    }
}