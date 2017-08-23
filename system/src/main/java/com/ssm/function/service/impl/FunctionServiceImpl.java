package com.ssm.function.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.ssm.cache.impl.HashStringRedisCacheGroup;
import com.ssm.cache.impl.RoleResourceCache;
import com.ssm.core.ILanguageProvider;
import com.ssm.core.request.IRequest;
import com.ssm.core.request.impl.RequestHelper;
import com.ssm.function.dto.Function;
import com.ssm.function.dto.FunctionDisplay;
import com.ssm.function.dto.FunctionResource;
import com.ssm.function.dto.MenuItem;
import com.ssm.function.dto.Resource;
import com.ssm.function.mapper.FunctionMapper;
import com.ssm.function.mapper.FunctionResourceMapper;
import com.ssm.function.mapper.RoleFunctionMapper;
import com.ssm.function.service.IFunctionService;
import com.ssm.function.service.IResourceService;
import com.ssm.function.service.IRoleFunctionService;
import com.ssm.sys.dto.DTOStatus;
import com.ssm.sys.dto.Language;
import com.ssm.sys.service.impl.BaseServiceImpl;


/**
 * 功能服务接口实现.
 * 
 * @author wuyichu
 */
@Transactional
@Service
public class FunctionServiceImpl extends BaseServiceImpl<Function> implements IFunctionService {

    @Autowired
    private FunctionMapper functionMapper;

    @Autowired
    private FunctionResourceMapper functionResourceMapper;

    @Autowired
    private RoleFunctionMapper roleFunctionMapper;

    @Autowired
    private IResourceService resourceService;

    @Autowired
    private IRoleFunctionService roleFunctionService;

    @Autowired
    @Qualifier("functionCache")
    private HashStringRedisCacheGroup<Function> functionCache;

    @Autowired
    private RoleResourceCache roleResourceCache;

    @Autowired
    private ILanguageProvider languageProvider;

    /**
     * 新增功能.
     * 
     * @param request
     *            上下文请求
     * @param function
     *            功能
     * @return 返回新增后的功能信息
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Function insertSelective(IRequest request, Function function) {
        if (function == null) {
            return null;
        }
        super.insertSelective(request, function);
        reloadFunctionCache(function.getFunctionId());
        return function;
    }

    private void reloadFunctionCache(Long functionId) {
        IRequest old = RequestHelper.getCurrentRequest();
        try {
            IRequest iRequest = RequestHelper.newEmptyRequest();
            RequestHelper.setCurrentRequest(iRequest);
            for (Language language : languageProvider.getSupportedLanguages()) {
                iRequest.setLocale(language.getLangCode());
                Function f = functionMapper.selectByPrimaryKey(functionId);
                if (f != null) {
                    functionCache.getGroup(language.getLangCode()).setValue(functionId.toString(), f);
                }
            }
        } finally {
            RequestHelper.setCurrentRequest(old);
        }
    }

    private void deleteFunctionCache(Long functionId) {
        for (Language language : languageProvider.getSupportedLanguages()) {
            functionCache.getGroup(language.getLangCode()).remove(functionId.toString());
        }
    }

    /**
     * 功能修改.
     * 
     * @param request
     *            上下文请求
     * @param function
     *            功能
     * @return 修改后的功能信息
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Function updateByPrimaryKey(IRequest request, Function function) {
        if (function == null) {
            return null;
        }
        super.updateByPrimaryKey(request, function);
        reloadFunctionCache(function.getFunctionId());
        return function;
    }

    /**
     * 根据ID查询功能.
     * 
     * @param request
     *            上下文请求
     * @param function
     *            function with ID
     * @return 功能信息
     */
    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public Function selectByPrimaryKey(IRequest request, Function function) {
        if (function.getFunctionId() == null) {
            return null;
        }
        Function result = functionMapper.selectByPrimaryKey(function.getFunctionId());
        return result;
    }

    /**
     * 删除功能.
     * 
     * @param request
     *            上下文请求
     * @param function
     *            功能
     * @return 删除的行数
     */
    private int deleteByPrimaryKey(IRequest request, Function function) {
        if (function.getFunctionId() == null) {
            return 0;
        }
        functionResourceMapper.deleteByFunctionId(function.getFunctionId());
        roleFunctionMapper.deleteByFunctionId(function.getFunctionId());
        deleteFunctionCache(function.getFunctionId());
        return functionMapper.deleteByPrimaryKey(function);
    }

    /**
     * 批量新增或修改.
     * 
     * @param request
     *            上下文请求
     * @param functions
     *            功能集合
     * @return 修改或新增过后的功能信息集合
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public List<Function> batchUpdate(IRequest request, List<Function> functions) {
        if (functions == null || functions.isEmpty()) {
            return functions;
        }
        for (Function function : functions) {
            if (function.getFunctionId() == null) {
                self().insertSelective(request, function);
            } else {
                self().updateByPrimaryKey(request, function);
            }
        }
        return functions;
    }

    /**
     * 批量删除.
     * 
     * @param request
     *            上下文请求
     * @param functions
     *            功能集合
     * @return 删除的函数
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public int batchDelete(IRequest request, List<Function> functions) {
        int result = 0;
        if (functions == null || functions.isEmpty()) {
            return result;
        }

        for (Function function : functions) {
            deleteByPrimaryKey(request, function);
            result++;
        }
        return result;
    }

    /**
     * 根据功能条件查询.
     * 
     * @param request
     *            上下文请求
     * @param example
     *            请求参数
     * @param page
     *            页码
     * @param pageSize
     *            页数
     * @return 满足条件的功能
     */
    @Override
    public List<FunctionDisplay> selectFunction(IRequest request, Function example, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        List<FunctionDisplay> list = functionMapper.selectFunctions(example);
        List<Function> allFunctions = functionCache.getGroup(request.getLocale()).getAll();
        Map<Long, Function> idFuncMap = new HashMap<>();
        allFunctions.forEach(f -> idFuncMap.put(f.getFunctionId(), f));
        list.forEach((function) -> {
            Function parent = idFuncMap.get(function.getParentFunctionId());
            if (parent != null) {
                function.setParentFunctionName(parent.getFunctionName());
            }
        });
        return list;
    }

    /**
     * 查询function挂靠的resource.
     * 
     * @param request
     *            上下文请求
     * @param function
     *            功能
     * @param resource
     *            资源
     * @param page
     *            页码
     * @param pageSize
     *            页数
     * @return 满足条件的resource集合
     */
    @Override
    public List<Resource> selectExitResourcesByFunction(IRequest request, Function function, Resource resource,
            int page, int pageSize) {
        if (function == null || function.getFunctionId() == null) {
            return null;
        }
        PageHelper.startPage(page, pageSize);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("function", function);
        params.put("resource", resource);
        return functionMapper.selectExistsResourcesByFunction(params);
    }

    /**
     * 查询function没有挂靠的resource.
     * 
     * @param request
     *            上下文请求
     * @param function
     *            功能
     * @param resource
     *            资源
     * @param page
     *            页码
     * @param pageSize
     *            页数
     * @return 返回满足条件的资源
     */
    @Override
    public List<Resource> selectNotExitResourcesByFunction(IRequest request, Function function, Resource resource,
            int page, int pageSize) {
        if (function == null || function.getFunctionId() == null) {
            return null;
        }
        PageHelper.startPage(page, pageSize);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("function", function);
        params.put("resource", resource);
        return functionMapper.selectNotExistsResourcesByFunction(params);
    }

    /**
     * 修改功能挂靠的resource.
     * 
     * @param request
     *            上下文请求
     * @param function
     *            功能
     * @param resources
     *            资源集合
     * @return 修改后的功能信息
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Function updateFunctionResources(IRequest request, Function function, List<Resource> resources) {
        if (function != null) {
            if (resources != null && !resources.isEmpty()) {
                for (Resource resource : resources) {
                    if (DTOStatus.ADD.equals(resource.get__status())) {
                        FunctionResource functionResource = new FunctionResource();
                        functionResource.setResourceId(resource.getResourceId());
                        functionResource.setFunctionId(function.getFunctionId());
                        functionResource.setObjectVersionNumber(1L);
                        functionResource.setCreatedBy(request.getUserId());
                        functionResource.setCreationDate(new Date());
                        functionResource.setLastUpdateDate(new Date());
                        functionResource.setLastUpdatedBy(request.getUserId());
                        functionResourceMapper.insertSelective(functionResource);
                    } else if (DTOStatus.DELETE.equals(resource.get__status())) {
                        functionResourceMapper.deleteFunctionResource(function.getFunctionId(),
                                resource.getResourceId());
                    }
                }
            }
            roleResourceCache.reload();
        }
        return function;
    }

    public List<MenuItem> selectAllMenus(IRequest request) {
        List<Function> functions = functionCache.getGroupAll(request.getLocale());
        MenuItem root = castFunctionsToMenuItem(functions);
        return root.getChildren();
    }

    private MenuItem castFunctionsToMenuItem(List<Function> functions) {
        MenuItem root = new MenuItem();
        List<MenuItem> children = new ArrayList<>();
        root.setChildren(children);

        Map<Long, Function> idToFuncMap = new HashMap<>();
        for (Function f : functions) {
            idToFuncMap.put(f.getFunctionId(), f);
        }

        Map<Long, MenuItem> map = new HashMap<>();
        Iterator<Function> iterator = functions.iterator();
        while (iterator.hasNext()) {
            Function function = iterator.next();
            if (idToFuncMap.get(function.getParentFunctionId()) == null) {
                MenuItem rootChild = createMenuItem(function);
                map.put(function.getFunctionId(), rootChild);
                children.add(rootChild);
                iterator.remove();
            }
        }

        processFunctions(map, functions);
        map.forEach((k, v) -> {
            if (v.getChildren() != null) {
                Collections.sort(v.getChildren());
            }
        });
        Collections.sort(children);
        return root;
    }

    private void processFunctions(Map<Long, MenuItem> map, List<Function> functions) {
        Iterator<Function> iterator = functions.iterator();
        int size0 = functions.size();
        while (iterator.hasNext()) {
            Function function = iterator.next();
            MenuItem parent = map.get(function.getParentFunctionId());
            if (parent != null) {
                MenuItem item = createMenuItem(function);
                map.put(function.getFunctionId(), item);
                List<MenuItem> children = parent.getChildren();
                if (children == null) {
                    children = new ArrayList<>();
                    parent.setChildren(children);
                }
                children.add(item);
                iterator.remove();
            }
        }

        if (functions.size() == size0) {
            // 功能定义存在循环
            detectCircle(functions);
        }

        if (!functions.isEmpty()) {
            processFunctions(map, functions);
        }
    }

    /**
     * 检测循环链
     * 
     * @param functions
     */
    private void detectCircle(List<Function> functions) {
        Map<Long, Function> tmpFuncMap = new HashMap<>();
        for (Function f : functions) {
            tmpFuncMap.put(f.getFunctionId(), f);
        }
        List<Function> tmpList = new ArrayList<>();
        for (Function f : functions) {
            tmpList.clear();
            Function f0 = f;
            tmpList.add(f0);
            while (f0.getParentFunctionId() != null) {
                f0 = tmpFuncMap.get(f0.getParentFunctionId());
                int idx = tmpList.indexOf(f0);
                if (idx != -1) {
                    tmpList.add(f0);
                    String msg = tmpList.stream().skip(idx)
                            .map(a -> a.getFunctionName() + "(" + a.getFunctionId() + ")")
                            .reduce((a, b) -> a + "-->" + b).get();
                    throw new RuntimeException(msg);
                }
                tmpList.add(f0);
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see IFunctionService#selectMenus(com.lkkhpg.dsis.hap.core. IRequest)
     */
    @Override
    public List<MenuItem> selectRoleFunctions(IRequest request) {
        List<Function> functions = functionCache.getGroupAll(request.getLocale());
        // Long[] ids = null;
        // ids = roleFunctionService.getRoleFunctionById(request.getRoleId());
        Set<Long> ids = new HashSet<Long>();
        // 取出该用户下所有角色的功能ID集合
        Long[] roleIds = request.getAllRoleId();
        for (Long roleId : roleIds) {
            Long[] fids = roleFunctionService.getRoleFunctionById(roleId);
            if (fids != null) {
                ids.addAll(Arrays.asList(fids));
            }
        }
        // ids = sids.toArray(new Long[sids.size()]);
        Map<Long, Function> funcMap = new HashMap<>();
        if (functions != null) {
            for (Function f : functions) {
                funcMap.put(f.getFunctionId(), f);
            }
        }
        Map<Long, MenuItem> menuMap = new HashMap<>();
        if (ids != null) {
            for (Long fId : ids) {
                createMenuRecursive(menuMap, funcMap, fId);
            }
        }
        List<MenuItem> itemList = new ArrayList<>();
        menuMap.forEach((k, v) -> {
            if (v.getParent() == null) {
                itemList.add(v);
            }
            if (v.getChildren() != null) {
                Collections.sort(v.getChildren());
            }
        });
        Collections.sort(itemList);
        return itemList;
    }

    private MenuItem createMenuRecursive(Map<Long, MenuItem> menuMap, Map<Long, Function> funcMap, Long funcId) {
        MenuItem mi = menuMap.get(funcId);
        if (mi == null) {
            Function func = funcMap.get(funcId);
            if (func == null) {
                // role has a function that dose not exists.
                return null;
            }
            mi = createMenuItem(func);
            menuMap.put(funcId, mi);
            // create parent mi
            Long parentId = func.getParentFunctionId();
            if (parentId != null) {
                MenuItem miParent = createMenuRecursive(menuMap, funcMap, parentId);
                if (miParent != null) {
                    List<MenuItem> children = miParent.getChildren();
                    if (children == null) {
                        children = new ArrayList<>();
                        miParent.setChildren(children);
                    }
                    mi.setParent(miParent);
                    children.add(mi);
                }
            }
        }
        return mi;
    }

    private MenuItem createMenuItem(Function function) {
        MenuItem menu = new MenuItem();
        menu.setText(function.getFunctionName());
        menu.setIcon(function.getFunctionIcon());
        menu.setFunctionCode(function.getFunctionCode());
        if (function.getResourceId() != null) {
            Resource resource = resourceService.selectResourceById(function.getResourceId());
            if (resource != null) {
                menu.setUrl(resource.getUrl());
            }
        }
        menu.setId(function.getFunctionId());
        menu.setScore(function.getFunctionSequence());
        return menu;
    }

}
