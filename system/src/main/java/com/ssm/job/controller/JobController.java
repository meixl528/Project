package com.ssm.job.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ssm.job.dto.JobCreateDto;
import com.ssm.job.dto.JobData;
import com.ssm.job.service.IQuartzService;
import com.ssm.sys.controller.BaseController;
import com.ssm.sys.responceFactory.ResponseData;

/**
 * 测试 job
 * @author meixl
 *
 */
@SuppressWarnings("deprecation")
@Controller
public class JobController extends BaseController{

	@Autowired
	private IQuartzService quartzService;		
			
	@RequestMapping(value = "/quartz/create/test")
	@ResponseBody
	public ResponseData createJob(JobCreateDto jobCreateDto, BindingResult result,
			HttpServletRequest request) throws Exception {
		jobCreateDto.setJobName("test_job");
		jobCreateDto.setJobGroup("DEFAULT");
		jobCreateDto.setJobClassName("com.ssm.job.jobs.NewJob");
		
		jobCreateDto.setTriggerGroup(jobCreateDto.getJobGroup());
		jobCreateDto.setTriggerName(jobCreateDto.getJobName() + "_trigger");
		jobCreateDto.setTriggerType("SIMPLE");
		
		jobCreateDto.setStart(new Date(2017,6,20));
		jobCreateDto.setEnd(new Date(2018, 6, 30));
		jobCreateDto.setRepeatCount("5");
		jobCreateDto.setRepeatInterval("120");
		List<JobData> jobDatas = new ArrayList<>();
		JobData job = new JobData();
		job.setName("job_internal_notification");
		job.setValue("false");
		jobDatas.add(job);
		
		job.setName("job_internal_emailAddress");
		job.setValue("");
		jobDatas.add(job);
		jobCreateDto.setJobDatas(jobDatas);
		System.out.println("jobCreateDto = " + jobCreateDto.toString());
		quartzService.createJob(jobCreateDto);

		ResponseData resp = new ResponseData();
		resp.setMessage("quartz create ok");
		return resp;
	}

}
