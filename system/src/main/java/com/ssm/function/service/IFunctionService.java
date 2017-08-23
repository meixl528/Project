package com.ssm.function.service;

import java.util.List;

import com.ssm.core.annotation.StdWho;
import com.ssm.core.proxy.ProxySelf;
import com.ssm.core.request.IRequest;
import com.ssm.function.dto.Function;
import com.ssm.function.dto.FunctionDisplay;
import com.ssm.function.dto.MenuItem;
import com.ssm.function.dto.Resource;
import com.ssm.sys.service.IBaseService;

public interface IFunctionService extends IBaseService<Function>, ProxySelf<IFunctionService>{
	
	/**
     * 查询所有菜单.
     * 
     * @param request
     *            上下文请求
     * @return 返回所有的菜单
     */
    List<MenuItem> selectAllMenus(IRequest request);

    /**
     * 查询当前角色的菜单.
     * 
     * @param request
     *            上下文请求
     * @return 返回当前角色的菜单集合
     */
    List<MenuItem> selectRoleFunctions(IRequest request);

    /**
     * 根据功能条件查询.
     * 
     * @param request
     *            上下文请求
     * @param function
     *            请求参数
     * @param page
     *            页码
     * @param pageSize
     *            页数
     * @return 满足条件的功能
     */
    List<FunctionDisplay> selectFunction(IRequest request, Function function, int page, int pageSize);

    /**
     * 批量新增或修改.
     * 
     * @param request
     *            上下文请求
     * @param functions
     *            功能集合
     * @return 修改或新增过后的功能信息集合
     */
    List<Function> batchUpdate(IRequest request, @StdWho List<Function> functions);

    /**
     * 批量删除.
     * 
     * @param request
     *            上下文请求
     * @param functions
     *            功能集合
     * @return 删除的函数
     */
    int batchDelete(IRequest request, List<Function> functions);

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
    List<Resource> selectExitResourcesByFunction(IRequest request, Function function, Resource resource, int page,
            int pageSize);

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
    List<Resource> selectNotExitResourcesByFunction(IRequest request, Function function, Resource resource, int page,
            int pageSize);

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
    Function updateFunctionResources(IRequest request, Function function, List<Resource> resources);

}
