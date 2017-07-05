package com.ssm.job.service;

import java.util.List;

import org.quartz.SchedulerException;

import com.ssm.job.dto.JobCreateDto;
import com.ssm.job.dto.JobDetailDto;
import com.ssm.job.dto.TriggerDto;

public interface IQuartzService{

	void createJob(JobCreateDto jobCreateDto) throws Exception;
	
	void pauseJobs(List<JobDetailDto> list) throws SchedulerException;

	void resumeJobs(List<JobDetailDto> list) throws SchedulerException;

	void deleteJobs(List<JobDetailDto> list) throws SchedulerException;

	void pauseTriggers(List<TriggerDto> list) throws SchedulerException;

	void resumeTriggers(List<TriggerDto> list) throws SchedulerException;
	
	
	//测试spring scheduler
	void testScheduler();
}
