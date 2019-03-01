package tech.dynamo.processor.record.batch;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;

import tech.dynamo.core.domain.ClientExchangeTransfer;
import tech.dynamo.core.service.RecordService;

public class TransferRecordItemWriter implements ItemWriter<ClientExchangeTransfer> {
	
	@Value("#{jobExecutionContext[jobId]}") private Long jobId;
	@Value("#{jobParameters[runId]}") private Long runId;
	
	@Resource private RecordService recordService;

	@Override
	public void write(List<? extends ClientExchangeTransfer> items) throws Exception {
		
		for (ClientExchangeTransfer item : items)
			recordService.createRecord(item, runId);
	}

}
