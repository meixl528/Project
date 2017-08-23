package com.ssm.publicTest.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class EchartsController {

	@RequestMapping(value = "/echarts/test")
	@ResponseBody
	public Map<String,Object> get(){
		Map<String,Object> map = new HashMap<>();
		
		String[] xAxisData = {"1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月"};
		map.put("xAxisData", xAxisData);
		
		List<String> legendData = new ArrayList<>();//{"蒸发量", "降水量"};
		List<Rains> rains = this.text();
		for (int i = 0; i < rains.size(); i++) {
			Rains rain = rains.get(i);
			legendData.add(rain.titleType);
			map.put("legendData", legendData);
			
			List<Double> seriesData = new ArrayList<>();
			seriesData.add(rain.month1);
			seriesData.add(rain.month2);
			seriesData.add(rain.month3);
			seriesData.add(rain.month4);
			seriesData.add(rain.month5);
			seriesData.add(rain.month6);
			
			seriesData.add(rain.month7);
			seriesData.add(rain.month8);
			seriesData.add(rain.month9);
			seriesData.add(rain.month10);
			seriesData.add(rain.month11);
			seriesData.add(rain.month12);
			
			map.put("series"+i, seriesData);
		}
		return map;
	}
	
	
	private List<Rains> text(){
		List<Rains> list = new ArrayList<>();
		
		Rains rain = new Rains("蒸发量",2.0, 4.9, 7.0, 23.2, 25.6, 76.7, 135.6, 162.2, 32.6, 20.0, 6.4, 3.3);
		
		Rains rain2 = new Rains("降水量",2.6, 5.9, 9.0, 26.4, 28.7, 70.7, 175.6, 182.2,48.7, 18.8, 6.0, 2.3);
		
		list.add(rain);
		list.add(rain2);
		
		return list;
	}
	
}

class Rains {
	
	public String titleType;
	public double month1;
	public double month2;
	public double month3;
	public double month4;
	public double month5;
	public double month6;
	
	public double month7;
	public double month8;
	public double month9;
	public double month10;
	public double month11;
	public double month12;
	public Rains(){}
	public Rains(String titleType, double month1, double month2, double month3, double month4, double month5,
			double month6, double month7, double month8, double month9, double month10, double month11,
			double month12) {
		this.titleType = titleType;
		this.month1 = month1;
		this.month2 = month2;
		this.month3 = month3;
		this.month4 = month4;
		this.month5 = month5;
		this.month6 = month6;
		this.month7 = month7;
		this.month8 = month8;
		this.month9 = month9;
		this.month10 = month10;
		this.month11 = month11;
		this.month12 = month12;
	}
	
}
