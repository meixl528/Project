package com.ssm.websocket.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.socket.TextMessage;

import com.ssm.account.dto.User;
import com.ssm.account.service.IUserService;
import com.ssm.websocket.Constants;
import com.ssm.websocket.service.impl.SystemWebSocketHandler;
/**
 * @name        WebSocketContrller
 * @description spring websocket
 * @author      meixl
 * @date        2017年7月28日下午4:57:29
 * @version
 */
@Controller
public class WebSocketContrller {

	@Autowired
    private IUserService userService;
	
	@Bean
    public SystemWebSocketHandler systemWebSocketHandler() {
        return new SystemWebSocketHandler();
    }

	/**
	 * 发送给单个
	 */
    @RequestMapping("/testWebSocket/sendToUser")
    @ResponseBody
    public String sendToUser(HttpServletRequest request){
        
        systemWebSocketHandler().sendMessageToUser(Constants.MAIL, (String)request.getSession().getAttribute(User.FIELD_USER_NAME), new TextMessage("0"));
        return "ok one";
    }
    
    /**
     * 发送给全部
     */
    @RequestMapping("/testWebSocket/sendToAll")
    @ResponseBody
    public String sendToAll(HttpServletRequest request){
        
    	List<User> list = userService.selectAll();
        systemWebSocketHandler().sendMessageToUsers(Constants.MESSAGE, new TextMessage(list.size() + ""));
        return "ok all "+list.size();
    }
	
}
