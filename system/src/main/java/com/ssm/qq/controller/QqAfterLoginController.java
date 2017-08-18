package com.ssm.qq.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.qq.connect.QQConnectException;
import com.qq.connect.api.OpenID;
import com.qq.connect.api.qzone.UserInfo;
import com.qq.connect.javabeans.AccessToken;
import com.qq.connect.javabeans.qzone.UserInfoBean;
import com.qq.connect.oauth.Oauth;

/**
 * Date: 12-12-4 Time: 下午4:36
 */
@Controller
public class QqAfterLoginController {
	private boolean loginSuccess;
	private String openId;
	private String nickName;
	
	private static final Logger logger = Logger.getLogger(QqAfterLoginController.class);

	@RequestMapping(value="/QqAfterLogin")
	public void QqAfterLogin(HttpServletRequest request, HttpServletResponse response) throws QQConnectException, IOException {
		AccessToken accessTokenObj = new Oauth().getAccessTokenByRequest(request);
		String accessToken = null;

		if (accessTokenObj.getAccessToken().equals("")) {
			// 我们的网站被CSRF攻击了或者用户取消了授权
			// 做一些数据统计工作
			loginSuccess = false; // 没有获取到响应参数
		} else {
			accessToken = accessTokenObj.getAccessToken();
			//tokenExpireIn = accessTokenObj.getExpireIn();

			// 利用获取到的accessToken 去获取当前用的openid -------- start
			OpenID openIDObj = new OpenID(accessToken);
			openId = openIDObj.getUserOpenID();

			UserInfo qzoneUserInfo = new UserInfo(accessToken, openId);
			UserInfoBean userInfoBean = qzoneUserInfo.getUserInfo();
			if (userInfoBean.getRet() == 0) {
				nickName = userInfoBean.getNickname();

			} else {
				logger.info("Get UserInfoBean fail: " + userInfoBean.getMsg());
			}
			loginSuccess = true;
		}
		
		System.out.println("openId +"+openId);
	}

	public boolean getLoginSuccess() {
		return loginSuccess;
	}

	public String getNickName() {
		return nickName;
	}

	public String getOpenId() {
		return openId;
	}
	
}
