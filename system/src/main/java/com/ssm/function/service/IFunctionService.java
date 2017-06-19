package com.ssm.function.service;

import java.util.List;

import com.ssm.core.proxy.ProxySelf;
import com.ssm.core.request.IRequest;
import com.ssm.function.dto.Function;
import com.ssm.function.dto.FunctionDisplay;
import com.ssm.function.dto.MenuItem;
import com.ssm.function.dto.Resource;
import com.ssm.sys.service.IBaseService;

public interface IFunctionService extends IBaseService<Function>, ProxySelf<IFunctionService>{
	
	List<MenuItem> selectRoleFunctions(IRequest request);

	List<?> selectExitResourcesByFunction(IRequest requestContext, Function function, Resource resource, int page,int pagesize);

	List<FunctionDisplay> selectFunction(IRequest request, Function example, int page, int pageSize);

	List<Function> submitFunction(IRequest request, List<Function> functions);

	int deleteFunction(IRequest requestContext, List<Function> functions);

	/**
	 * 查询所有菜单
	 * @param requestContext
	 * @return
	 */
	List<MenuItem> selectAllMenus(IRequest requestContext);

}
