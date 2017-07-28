package com.ssm.job.service.impl;

import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.ScheduleBuilder;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.ssm.activeMQ.service.impl.MessageSender;
import com.ssm.job.AbstractJob;
import com.ssm.job.dto.JobCreateDto;
import com.ssm.job.dto.JobData;
import com.ssm.job.dto.JobDetailDto;
import com.ssm.job.dto.TriggerDto;
import com.ssm.job.service.IQuartzService;

@Component
@EnableScheduling
@Lazy(false) 
@Service
public class QuartzServiceImpl implements IQuartzService{
	
	private final Logger logger = LoggerFactory.getLogger(QuartzServiceImpl.class);
	@Autowired
	private Scheduler quartzScheduler;
	
	
	// 1) 为Quartz的重要组件类提供更具Bean风格的扩展类；
	// 2) 提供创建Scheduler的BeanFactory类，方便在Spring环境下创建对应的组件对象，并结合Spring容器生命周期进行启动和停止的动作。
	/*@Autowired
	private SchedulerFactoryBean schedulerFactory;
	private Scheduler scheduler = schedulerFactory.getScheduler();*/
	/**
	 * 创建job
	 */
	public void createJob(JobCreateDto jobCreateDto) throws Exception {
        Assert.hasText(jobCreateDto.getJobClassName());
        Assert.hasText(jobCreateDto.getJobName());
        Assert.hasText(jobCreateDto.getJobGroup());
        Assert.hasText(jobCreateDto.getTriggerName());
        Assert.hasText(jobCreateDto.getTriggerGroup());
        Assert.hasText(jobCreateDto.getTriggerType());

        String jobClassName = jobCreateDto.getJobClassName();

        boolean assignableFrom = false;
        Class forName = null;
        try {
            forName = Class.forName(jobClassName);
            assignableFrom = AbstractJob.class.isAssignableFrom(forName);
        } catch (ClassNotFoundException e) {
            if (logger.isErrorEnabled()) {
                logger.error(e.getMessage(), e);
            }
        }
        if (!assignableFrom || forName == null) {
            String name = AbstractJob.class.getName();
            throw new Exception("JOB_EXCEPTION, NOT_SUB_CLASS");
        }

        // Class jobClass = Class.forName(jobCreateDto.getJobClassName());
        JobBuilder jb = JobBuilder.newJob(forName).withIdentity(jobCreateDto.getJobName(), jobCreateDto.getJobGroup())
                .withDescription(jobCreateDto.getDescription());

        if (hasJobData(jobCreateDto)) {
            JobDataMap data = new JobDataMap();
            List<JobData> jobDatas = jobCreateDto.getJobDatas();
            for (JobData jobData : jobDatas) {
                data.put(jobData.getName(), jobData.getValue());
            }
            jb = jb.usingJobData(data);
        }
        JobDetail jobDetail = jb.build();

        TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger()
                .withIdentity(jobCreateDto.getTriggerName(), jobCreateDto.getTriggerGroup()).forJob(jobDetail);
        if (jobCreateDto.getStartTime() != null && jobCreateDto.getStartTime() > 0) {
            triggerBuilder.startAt(new Date(jobCreateDto.getStartTime()));
            // triggerBuilder.startAt(jobCreateDto.getStart());
        }
        if (jobCreateDto.getEndTime() != null && jobCreateDto.getEndTime() > 0) {
            triggerBuilder.endAt(new Date(jobCreateDto.getEndTime()));
            // triggerBuilder.endAt(jobCreateDto.getEnd());
        }
        ScheduleBuilder sche = null;
        if ("CRON".equalsIgnoreCase(jobCreateDto.getTriggerType())) {
            Assert.hasText(jobCreateDto.getCronExpression());
            sche = CronScheduleBuilder.cronSchedule(jobCreateDto.getCronExpression());

        } else if ("SIMPLE".equalsIgnoreCase(jobCreateDto.getTriggerType())) {
            Assert.hasText(jobCreateDto.getRepeatInterval());
            int interval = Integer.parseInt(jobCreateDto.getRepeatInterval());
            int count = 0;
            try {
                count = Integer.parseInt(jobCreateDto.getRepeatCount());
            } catch (Throwable thr) {
            }
            if (count < 1) {
                sche = SimpleScheduleBuilder.repeatSecondlyForever(interval);
            } else {
                sche = SimpleScheduleBuilder.repeatSecondlyForTotalCount(count, interval);
            }

        }
        Trigger trigger = triggerBuilder.withSchedule(sche).build();
        quartzScheduler.scheduleJob(jobDetail, trigger);
    }
	
	private boolean hasJobData(JobCreateDto jobCreateDto) {
        List<JobData> jobDatas = jobCreateDto.getJobDatas();
        return jobDatas != null && !jobDatas.isEmpty();
    }
	
	/**
	 * 暂停job
	 */
	@Override
    public void pauseJobs(List<JobDetailDto> list) throws SchedulerException {
        for (JobDetailDto job : list) {
            quartzScheduler.pauseJob(JobKey.jobKey(job.getJobName(), job.getJobGroup()));
        }
    }
	
	/**
	 * 继续执行job
	 */
	@Override
    public void resumeJobs(List<JobDetailDto> list) throws SchedulerException {
        for (JobDetailDto job : list) {
            quartzScheduler.resumeJob(JobKey.jobKey(job.getJobName(), job.getJobGroup()));
        }
    }
	
	/**
	 * 删除job
	 */
	@Override
    public void deleteJobs(List<JobDetailDto> list) throws SchedulerException {
        for (JobDetailDto job : list) {
            quartzScheduler.deleteJob(JobKey.jobKey(job.getJobName(), job.getJobGroup()));
        }
    }
	
	/**
	 * 暂停触发器
	 * @param list
	 */
	@Override
    public void pauseTriggers(List<TriggerDto> list) throws SchedulerException {
        for (TriggerDto trigger : list) {
            quartzScheduler.pauseTrigger(TriggerKey.triggerKey(trigger.getTriggerName(), trigger.getTriggerGroup()));
        }
    }
	
	/**
	 * 继续执行触发器
	 * @param list
	 */
	@Override
    public void resumeTriggers(List<TriggerDto> list) throws SchedulerException {
        for (TriggerDto trigger : list) {
            quartzScheduler.resumeTrigger(TriggerKey.triggerKey(trigger.getTriggerName(), trigger.getTriggerGroup()));
        }
    }

	@Autowired
    private transient  MessageSender messageSender;
	
	// @Scheduled(cron="30 * * * * ?") 
	@Override
	public void testScheduler() {
		if(logger.isInfoEnabled()){
			logger.info("测试spring scheduler 运行 -> com.ssm.job.service.impl.QuartzServiceImpl :" +new DateTime().toString("yyyy-MM-dd hh:mm:ss SSS"));
			//testMonitorService.onQueueMessage("userCache", "queue:cache:reload");
		}
		messageSender.sendQueue("queue测试 "+new org.joda.time.DateTime().toString("yyyy-MM-dd HH:mm:ss"),"rest:queue:test");
		messageSender.sendTopic("topic测试  "+new org.joda.time.DateTime().toString("yyyy-MM-dd HH:mm:ss"),"soap:topic:test","soap:topic:test2");
		
	}
	
	
	
	
}
