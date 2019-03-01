package tech.dynamo.processor.record.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.stereotype.Service;

import tech.dynamo.core.service.RunService;
import tech.dynamo.processor.commons.service.ProcessorException;

@Service
public class RecordProcessorServiceImpl implements RecordProcessorService {

	private static final Long RUN_TYPE_ID = 2000L;
	
	@Resource private RunService runService;
	@Resource private JobLauncher jobLauncher;
	@Resource private Job jobRecordProcessorBatch;
	
	@Override
	public Long processAll() {

		return doProcess(null);
	}

	@Override
	public Long processForEntity(Long entityId) {
		
		return doProcess(entityId); 
	}	
	
	private Long doProcess(Long entityId) {
		try {
			JobExecution jobExecution = jobLauncher.run(jobRecordProcessorBatch, getParameters(entityId));
			return jobExecution.getId();
		} catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException
				| JobParametersInvalidException _ex) {
			throw new ProcessorException(_ex);
		}			
	}
	
	private JobParameters getParameters(Long entityId) {
		
		Map<String, JobParameter> params = new HashMap<>();
		
		if (entityId != null) params.put("entityId", new JobParameter(entityId));
		params.put("unique", new JobParameter(new Date().getTime()));
		params.put("runId", new JobParameter(runService.create(RUN_TYPE_ID)));
		
		return new JobParameters(params);
	}

}
