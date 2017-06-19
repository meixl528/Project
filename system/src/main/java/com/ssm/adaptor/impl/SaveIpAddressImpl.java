package com.ssm.adaptor.impl;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ssm.account.dto.User;
import com.ssm.adaptor.ISaveIpAddressListener;
import com.ssm.core.request.IRequest;
import com.ssm.sys.dto.UserLoginDetail;
import com.ssm.sys.mapper.UserLoginDetailMapper;

/**
 * @description  获取登录者ip地址信息
 * @author meixl	2017年4月10日上午10:19:22
 */
@Component
public class SaveIpAddressImpl implements ISaveIpAddressListener {

	//@Autowired
	//private SqlSessionFactory sqlSessionFactory;
	
	@Autowired
	private UserLoginDetailMapper userLoginDetailMapper;
	
	@Override
	public void onSaveIpAdress(HttpServletRequest request, HttpServletResponse response) {
		String ipAddress = getIpAddress(request);

        UserLoginDetail userLogin = new UserLoginDetail();
        userLogin.setUserId((Long)request.getSession(false).getAttribute(User.FIELD_USER_ID));
        String path = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getRequestURI();
        String referer = request.getHeader("Referer");
        referer = StringUtils.isBlank(referer)?path:referer;
        referer = StringUtils.abbreviate(referer,240);
        userLogin.setReferer(referer);
        userLogin.setUserAgent(request.getHeader("User-Agent"));
        userLogin.setIp(ipAddress);
        userLogin.setLoginTime(new Date());

        //存入数据库
        userLoginDetailMapper.insertSelective(userLogin);
        //sqlSessionFactory.openSession().insert("com.ssm.sys.mapper.UserLoginDetailMapper.saveDetail", userLogin);
        
        
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.setAttribute(IRequest.FIELD_LOGIN_ID, userLogin.getLoginId());
        }
	}
	
	public String getIpAddress(HttpServletRequest request) {

        String ipAddress = request.getHeader("x-forwarded-for");
        if(ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if(ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if(ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
            if(ipAddress.equals("127.0.0.1") || ipAddress.equals("0:0:0:0:0:0:0:1")){
                //根据网卡取本机配置的IP
                InetAddress inet=null;
                try {
                    inet = InetAddress.getLocalHost();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                ipAddress= inet.getHostAddress();
            }
        }
        //对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
        if(ipAddress!=null && ipAddress.length()>15){ //"***.***.***.***".length() = 15
            if(ipAddress.indexOf(",")>0){
                ipAddress = ipAddress.substring(0,ipAddress.indexOf(","));
            }
        }
        return ipAddress;

    }

}
