package com.ssm.websocket.service.impl;

import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import com.ssm.account.dto.User;
import com.ssm.websocket.Constants;

public class WebSocketHandshakeInterceptor implements HandshakeInterceptor {

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) 
    		throws Exception {
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
            HttpSession session = servletRequest.getServletRequest().getSession(false);
            if (session != null) {
                //使用userName区分WebSocketHandler，以便定向发送消息
                String userName = (String) session.getAttribute(User.FIELD_USER_NAME);
                attributes.put(User.FIELD_USER_NAME,userName);
                
                attributes.put(Constants.FLAG,servletRequest.getServletRequest().getParameter(Constants.FLAG));
            }
        }
        if(request.getHeaders().containsKey("Sec-WebSocket-Extensions")) {
        	request.getHeaders().set("Sec-WebSocket-Extensions", "permessage-deflate");
        }
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }
}