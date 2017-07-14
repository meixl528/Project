package com.ssm.job.jobs;

import org.quartz.JobExecutionContext;
import com.ssm.job.AbstractJob;

public class NewJob extends AbstractJob{

	@Override
	public void safeExecute(JobExecutionContext context) throws Exception {
		System.out.println("NewJob-----------"+context.getNextFireTime());
	}

	@Override
	public boolean isRefireImmediatelyWhenException() {
		return false;
	}

}
