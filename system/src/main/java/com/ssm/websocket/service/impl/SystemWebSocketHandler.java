package com.ssm.websocket.service.impl;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import com.ssm.account.dto.User;
import com.ssm.websocket.Constants;

public class SystemWebSocketHandler implements WebSocketHandler {

    private static final Logger logger = LoggerFactory.getLogger(SystemWebSocketHandler.class);

    //concurrent包的线程安全Set，用来存放每个客户端对应的WebSocketSession对象。若要实现服务端与单一客户端通信的话，可以使用Map来存放，其中Key可以为用户标识  
    private static final CopyOnWriteArraySet<WebSocketSession> users = new CopyOnWriteArraySet<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        logger.info("connect to the websocket success......");
        String userName = (String) session.getAttributes().get(User.FIELD_USER_NAME);
        if(userName!= null){
        	users.add(session);
        }
        String flag = (String) session.getAttributes().get(Constants.FLAG);
        if(flag.equals(Constants.MAIL)){
        	session.sendMessage(new TextMessage("9"));
        }else if(flag.equals(Constants.MESSAGE)){
        	session.sendMessage(new TextMessage("2"));
        }
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
    	System.out.println("message = "+message);
        //sendMessageToUsers();
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        if(session.isOpen()){
            session.close();
        }
        users.remove(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        users.remove(session);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    /**
     * 给所有在线用户发送消息
     *
     * @param message
     */
    public void sendMessageToUsers(String flag, TextMessage message) {
        for (WebSocketSession user : users) {
        	if(user.getAttributes().get(Constants.FLAG).equals(flag)){
        		try {
                    if (user.isOpen()) {
                        user.sendMessage(message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
        	}
        }
    }

    /**
     * 给某个用户发送消息
     *
     * @param userName
     * @param message
     */
    public void sendMessageToUser(String flag,String userName, TextMessage message) {
        for (WebSocketSession user : users) {
            if (user.getAttributes().get(User.FIELD_USER_NAME).equals(userName) && user.getAttributes().get(Constants.FLAG).equals(flag)) {
                try {
                    if (user.isOpen()) {
                        user.sendMessage(message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }
}