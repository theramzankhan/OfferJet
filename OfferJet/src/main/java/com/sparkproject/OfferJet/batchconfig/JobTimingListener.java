package com.sparkproject.OfferJet.batchconfig;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

@Component
public class JobTimingListener implements JobExecutionListener {
	
	private static final Logger logger = LoggerFactory.getLogger(JobTimingListener.class);

	long startTime;
	
	@Override
	public void beforeJob(JobExecution jobExecution) {
		startTime = System.currentTimeMillis();
		logger.info("===================  Job started =================== ");
	}
	
	@Override
	public void afterJob(JobExecution jobExecution) {
		long endTime = System.currentTimeMillis();
		long duration = endTime - startTime;
		logger.info("===================  Job completed in { "+ duration +" } ms =================== ", duration);
	}
}
