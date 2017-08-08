package com.ssm.account.service.impl;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssm.account.AccountConfig;
import com.ssm.account.Md5Util;
import com.ssm.account.dto.User;
import com.ssm.account.exception.UserException;
import com.ssm.account.mapper.UserMapper;
import com.ssm.account.service.IUserService;
import com.ssm.core.annotation.StdWho;
import com.ssm.core.request.IRequest;
import com.ssm.sys.service.impl.BaseServiceImpl;
/**
 * @name        UserServiceImpl
 * @description 
 * @author      meixl
 * @date        2017年6月19日上午9:19:55
 * @version
 */
@Service
public class UserServiceImpl extends BaseServiceImpl<User> implements IUserService{
	
	@Autowired
	private UserMapper userMapper;
	
	@Override
	public List<String> getAcceptedProfiles() {
		return Arrays.asList("DEFAULT_PASSWORD","PASSWORD_MIN_LENGTH","PASSWORD_COMPLEXITY");
	}

	@Override
	public void updateProfile(String profileName, String profileValue) {
		if(profileName.equals("DEFAULT_PASSWORD")){
			AccountConfig.DEFAULT_PASSWORD = profileValue;
		}else if(profileName.equals("PASSWORD_MIN_LENGTH")){
			AccountConfig.PASSWORD_MIN_LENGTH = Integer.valueOf(profileValue);
		}else if(profileName.equals("PASSWORD_COMPLEXITY")){
			AccountConfig.PASSWORD_COMPLEXITY = profileValue;
		}
	}
	

	/**
	 * 验证用户名是否存在
	 * @return
	 */
	public String validateUser(List<User> list){
		StringBuilder builder = new StringBuilder();
		for (User user : list) {
			if (user.getUserId() == null) {
				/** 查询录入时,用户名是否存在 */
            	User u = userMapper.selectByUserName(user.getUserName());
            	if(u!=null) 
            		builder.append(",").append(user.getUserName());
            }else if(user.getUserId() != null){
            	/** 查询修改时,用户名修改了的,是否存在 */
            	User uId = userMapper.selectByPrimaryKey(user);
            	if(!user.getUserName().equals(uId.getUserName())){
            		User uName = userMapper.selectByUserName(user.getUserName());
            		if(uName!=null) 
                		builder.append(",").append(user.getUserName());
            	}
            }
		}
		return builder.length() >0?builder.substring(1):null;
	}
	
	@Override
	@Transactional
	public List<User> submitUser(HttpServletRequest request,IRequest iRequest,@StdWho List<User> list) {
		for (User user : list) {
			if(StringUtils.isBlank(user.getPassword())){
				user.setPassword(Md5Util.MD5(AccountConfig.DEFAULT_PASSWORD));
			}
            if (user.getUserId() == null) {
            	userMapper.insertSelective(user);
            } else {
            	userMapper.updateByPrimaryKey(user);
            	HttpSession session = request.getSession();
            	if(session.getAttribute(User.FIELD_USER_ID).equals(user.getUserId())){
            		if(!session.getAttribute(User.FIELD_USER_NAME).equals(user.getUserName())){
            			session.setAttribute(User.FIELD_USER_NAME, user.getUserName());
            		}
            	}
            }
        }
        return list;
	}
	
	@Override
	@Transactional
	public int removeUser(List<User> list) {
		int result = 0;
        if (list == null || list.isEmpty()) {
            return result;
        }
        for (User user : list) {
        	userMapper.deleteByPrimaryKey(user);
            result++;
        }
        return result;
	}
	
	
	@Override
    public User login(User user) throws UserException {
        if (user == null || org.apache.commons.lang3.StringUtils.isAnyBlank(user.getUserName(), user.getPassword())) {
            throw new UserException(UserException.ERROR_USER_PASSWORD, UserException.ERROR_USER_PASSWORD, null);
        }
        User user1 = userMapper.selectByUserName(user.getUserName());
        if (user1 == null) {
            throw new UserException(UserException.ERROR_USER_PASSWORD, UserException.ERROR_USER_PASSWORD, null);
        }
        if (User.STATUS_LOCK.equals(user1.getStatus())) {
            throw new UserException(UserException.ERROR_USER_EXPIRED, UserException.ERROR_USER_EXPIRED, null);
        }
        if (user1.getStartActiveDate() != null && user1.getStartActiveDate().getTime() > System.currentTimeMillis()) {
            throw new UserException(UserException.ERROR_USER_EXPIRED, UserException.ERROR_USER_EXPIRED, null);
        }
        if (user1.getEndActiveDate() != null && user1.getEndActiveDate().getTime() < System.currentTimeMillis()) {
            throw new UserException(UserException.ERROR_USER_EXPIRED, UserException.ERROR_USER_EXPIRED, null);
        }
/*        if (!user.getPass().matches(user1.getPass())) {
            throw new UserException(UserException.ERROR_USER_PASSWORD, UserException.ERROR_USER_PASSWORD, null);
        }*/
        if (!Md5Util.MD5(user.getPassword()).matches(user1.getPassword())) {
            throw new UserException(UserException.ERROR_USER_PASSWORD, UserException.ERROR_USER_PASSWORD, null);
        }
        return user1;
    }

	@Override
	@Transactional
	public void updatePassword(Long userId, String password) {
		String passwordEncrypted = Md5Util.MD5(password);
		User u = new User();
		u.setUserId(userId);
		u.setPassword(passwordEncrypted);
		userMapper.updateByPrimaryKeySelective(u);
	}
	
}
