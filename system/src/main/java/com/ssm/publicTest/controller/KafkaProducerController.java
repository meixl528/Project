package com.ssm.publicTest.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ssm.kafka.service.KafkaProducerServer;
import com.ssm.sys.responceFactory.ResponseData;

@Controller
public class KafkaProducerController {
	
	@Autowired
	private KafkaProducerServer kafkaProducer;
	
	@RequestMapping(value="/kafka/test")
	@ResponseBody
    public ResponseData test(HttpServletRequest request,HttpServletResponse response,@RequestParam String param) throws Exception{
        
        String topic = "orderTopic";
        String value = "test "+new DateTime().toString("yyyy/MM/dd HH:mm:ss Sss");
        String ifPartition = "0";
        Integer partitionNum = 2;
        String role = "test";//用来生成key
        Map<String,Object> result = kafkaProducer.sndMesForTemplate(topic, value, ifPartition, partitionNum, role);
        
        System.out.println("测试结果如下：===============");
        System.out.println("code:"+(String)result.get("code"));
        System.out.println("message:"+(String)result.get("message"));
        
        //Map<String,Object> res2 = kafkaProducer.sndMesForTemplate("orderTopic2", "test2 "+new DateTime().toString("yyyy/MM/dd HH:mm:ss Sss"), ifPartition, partitionNum, "test2");
       
        ResponseData res = new ResponseData();
        res.setMessage((String) result.get("message"));
        return res;
    }
	
	
	@RequestMapping(value="/kafka/test2")
	@ResponseBody
    public ResponseData test2(HttpServletRequest request,HttpServletResponse response,@RequestParam String param2) throws Exception{
        ResponseData res = new ResponseData();
        return res;
    }
}