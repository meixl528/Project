package com.ssm.publicTest.controller;

import java.util.Map;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ssm.kafka.service.KafkaProducerServer;
import com.ssm.sys.responceFactory.ResponseData;

@Controller
public class KafkaProducerController {
	
	@Autowired
	private KafkaProducerServer kafkaProducer;
	
	@RequestMapping(value="/kafka/test")
	@ResponseBody
    public ResponseData test() throws Exception{
        
        String topic = "orderTopic";
        String value = "test "+new DateTime().toString("yyyy/MM/dd HH:mm:ss Sss");
        String ifPartition = "0";
        Integer partitionNum = 2;
        String role = "test";//用来生成key
        Map<String,Object> res = kafkaProducer.sndMesForTemplate(topic, value, ifPartition, partitionNum, role);
        
        System.out.println("测试结果如下：===============");
        System.out.println("code:"+(String)res.get("code"));
        System.out.println("message:"+(String)res.get("message"));
        
        //Map<String,Object> res2 = kafkaProducer.sndMesForTemplate("orderTopic2", "test2 "+new DateTime().toString("yyyy/MM/dd HH:mm:ss Sss"), ifPartition, partitionNum, "test2");
       
        ResponseData response = new ResponseData();
        response.setMessage((String) res.get("message"));
        return response;
    }
	
	
}