package com.ssm.mail.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ssm.mail.dto.Message;
import com.ssm.mail.mapper.MessageMapper;
import com.ssm.mail.service.IEmailService;
import com.ssm.sys.controller.BaseController;

@Controller
public class EmailController extends BaseController {

    @Autowired
    IEmailService emailService;

    @Autowired
    MessageMapper messageMapper;

    @RequestMapping(value = "/send_all_email")
    @ResponseBody
    public boolean sendAllEmail() {
        Map<String,Object> pa = new HashMap<>();
        pa.put("batch", 0);
        pa.put("isVipQueue", false);
        boolean result = false;
        try {
            result = emailService.sendMessages(pa);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    @RequestMapping(value = "/mail/resend_email")
    @ResponseBody
    public boolean reSendEmail(@RequestBody List<Message> messages) {
        boolean result = false;
        List<Message> newMessages = new ArrayList<>();
        for (Message mess : messages) {
            Message s1 = messageMapper.selectByPrimaryKey(mess);
            if (s1 != null && s1.getSendFlag().equals("F")) {
                newMessages.add(s1);
            }
        }
        try {
            result = emailService.reSendMessages(newMessages, new HashMap<>());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}