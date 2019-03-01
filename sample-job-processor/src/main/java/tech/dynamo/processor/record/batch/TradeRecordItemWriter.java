package tech.dynamo.processor.record.batch;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;

import tech.dynamo.core.domain.ClientExchangeTrade;
import tech.dynamo.core.service.RecordService;

public class TradeRecordItemWriter implements ItemWriter<ClientExchangeTrade> {
	
	@Value("#{jobExecutionContext[jobId]}") private Long jobId;
	@Value("#{jobParameters[runId]}") private Long runId;
	
	@Resource private RecordService recordService;

	@Override
	public void write(List<? extends ClientExchangeTrade> items) throws Exception {
		
		for (ClientExchangeTrade item : items)
			recordService.createRecord(item, runId);
	}

}
