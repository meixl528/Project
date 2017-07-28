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

    @RequestMapping("/testWebSocket/sendToUser")
    @ResponseBody
    public String sendToUser(HttpServletRequest request){
        //无关代码都省略了
        systemWebSocketHandler().sendMessageToUser((String)request.getSession().getAttribute("USER_NAME"), new TextMessage(" 发送来了"));
        return "ok one";
    }
    
    @RequestMapping("/testWebSocket/sendToAll")
    @ResponseBody
    public String sendToAll(HttpServletRequest request){
        //无关代码都省略了
    	List<User> list = userService.selectAll();
        systemWebSocketHandler().sendMessageToUsers(new TextMessage(list.size() + " 发送来了"));
        return "ok all "+list.size();
    }
	
}
