package com.ssm.account.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.support.RequestContextUtils;

import com.ssm.account.dto.User;
import com.ssm.account.exception.UserException;
import com.ssm.account.service.IUserService;
import com.ssm.core.exception.BaseException;
import com.ssm.core.request.IRequest;
import com.ssm.fnd.service.ISequenceService;
import com.ssm.sys.controller.BaseController;
import com.ssm.sys.responceFactory.ResponseData;
/**
 * @name        UserController
 * @description 
 * @author      meixl
 * @date        2017年6月19日上午9:18:26
 * @version
 */
@Controller
public class UserController extends BaseController{
	
	Logger logger =LoggerFactory.getLogger(UserController.class);
	
	@Autowired
    private IUserService userService;
	@Autowired
	private ISequenceService sequenceService;
	
	/**
	 * 用户名已存在    ex : 用户名:{0}已存在!
	 */
	public static final String USERNAME_EXIST = "UserName_Exist";
	
	/**
     * 初始化时间
     * @param binder
     */
    @InitBinder  
    public void initBinder(ServletRequestDataBinder binder) {  
	    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
	    dateFormat.setLenient(false);  
	    binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
     }

    /**
     * 查询用户数据.
     */
    @RequestMapping(value = "/sys/user/query", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData selectUsers(HttpServletRequest request,@ModelAttribute User user, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                    @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize) {
    	IRequest iRequest = createRequestContext(request);
    	return new ResponseData(userService.select(iRequest,user, page, pagesize));
    }
    
    /**
     * 查询用户数据.
     */
    @RequestMapping(value = "/sys/user/remove", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData removeUsers(@RequestBody final List<User> userList) {
    	userService.removeUser(userList);
    	return new ResponseData();
    }
    
    /**
     * 保存更新账户数据.
     * @throws BaseException
     */
    @RequestMapping(value = "/sys/user/submit", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData submitUsers(@RequestBody List<User> userList,HttpServletRequest request,final BindingResult result)throws BaseException {
    	getValidator().validate(userList, result);
    	if (result.hasErrors()) {
			ResponseData responseData = new ResponseData(false);
			responseData.setMessage(getErrorMessage(result, request));
			return responseData;
		}
    	
    	IRequest iRequest = createRequestContext(request);
    	
        String msg = userService.validateUser(userList);
        ResponseData  responseData = new ResponseData(true);
        if(msg!=null){
        	responseData.setSuccess(false);
        	responseData.setMessage(this.getMessageSource().getMessage(UserException.USER_EXIST, new Object[]{msg},RequestContextUtils.getLocale(request)));
        }else{
        	responseData.setRows(userService.submitUser(request,iRequest, userList));
        }
        return responseData;
    }
    
    /**
     * 重置密码
     * @param password
     * @param userId
     * @return
     */
    @RequestMapping(value = "/sys/user/resetPass", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData resetPassword(HttpServletRequest request,String pass,Long userId){
    	createRequestContext(request);
        userService.updatePassword(userId, pass);
        return new ResponseData(true);
    }

}
