package com.ssm.function.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ssm.core.exception.BaseException;
import com.ssm.core.request.IRequest;
import com.ssm.function.dto.MenuItem;
import com.ssm.function.dto.RoleFunction;
import com.ssm.function.service.IFunctionService;
import com.ssm.function.service.IRoleFunctionService;
import com.ssm.function.service.IRoleResourceItemService;
import com.ssm.sys.controller.BaseController;
import com.ssm.sys.responceFactory.ResponseData;

@Controller
public class RoleFunctionController extends BaseController {

    @Autowired
    private IRoleFunctionService roleFunctionService;

    @Autowired
    private IFunctionService functionService;

    @Autowired
    private IRoleResourceItemService roleResourceItemService;

    /**
     * 查询所有菜单,角色拥有的菜单选项打勾.
     *
     * @param request HttpServletRequest
     * @param roleId  roleId
     * @return ResponseData ResponseData
     * @throws BaseException BaseException
     */
    @RequestMapping(value = "/sys/rolefunction/query", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData selectRoleFuntion(HttpServletRequest request, @RequestParam(required = false) Long roleId)
            throws BaseException {
        IRequest requestContext = createRequestContext(request);
        List<MenuItem> menus = functionService.selectAllMenus(requestContext);
        if (roleId != null) {
            Long[] ids = roleFunctionService.getRoleFunctionById(roleId);
            updateMenuCheck(menus, ids);

        }
        return new ResponseData(menus);
    }

    /**
     * 处理勾选状态.
     *
     * @param menus 菜单
     * @param ids   functionId
     */
    private void updateMenuCheck(final List<MenuItem> menus, final Long[] ids) {
        if (menus == null || ids == null) {
            return;
        }
        for (MenuItem menuItem : menus) {
            if (menuItem.getChildren() != null && !menuItem.getChildren().isEmpty()) {
                updateMenuCheck(menuItem.getChildren(), ids);
            }
            for (Long id : ids) {
                if (menuItem.getId().equals(id)) {
                    menuItem.setIschecked(Boolean.TRUE);
                }
            }
        }
    }

    /**
     * 对角色分配的功能数据进行保存.
     *
     * @param request 请求上下文
     * @param records 角色功能
     * @return ResponseData ResponseData
     * @throws BaseException BaseException
     */
    @RequestMapping(value = "/sys/rolefunction/submit", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData submit(HttpServletRequest request, @RequestBody List<RoleFunction> records)
            throws BaseException {
        return new ResponseData(roleFunctionService.batchUpdate(createRequestContext(request), records));
    }


    /**
     * 获取资源权限项.
     *
     * @param request
     * @param roleId
     * @param functionId
     * @return ResponseData
     */
    @RequestMapping("/sys/rolefunction/queryResourceItems")
    @ResponseBody
    public ResponseData queryResourceItems(HttpServletRequest request, @RequestParam(required = false) Long roleId,
                                           @RequestParam(required = false) Long functionId) {
        return new ResponseData(roleResourceItemService.queryMenuItems(createRequestContext(request), roleId, functionId));
    }

    /**
     * 保存角色权限项.
     *
     * @param request
     * @param roleResourceItems
     * @param roleId
     * @param functionId
     * @return ResponseData
     */
   /* @RequestMapping("/sys/rolefunction/submitResourceItems")
    @ResponseBody
    public ResponseData submitResourceItems(HttpServletRequest request,
                                            @RequestBody List<RoleResourceItem> roleResourceItems, @RequestParam(required = false) Long roleId,
                                            @RequestParam(required = false) Long functionId) {
        return new ResponseData(roleResourceItemService.batchUpdate(createRequestContext(request), roleResourceItems,
                roleId, functionId));
    }*/
}
