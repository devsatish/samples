package tech.dynamo.processor.record.batch;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.beans.factory.annotation.Value;

import tech.dynamo.core.domain.ClientExchangeTransfer;
import tech.dynamo.core.repository.ClientExchangeTransferRepository;

public class TransferRecordItemReader implements ItemReader<ClientExchangeTransfer> {

	private static final int DEFAULT_UNPROCESSED_SIZE = 1000;
	
	@Value("#{jobParameters[entityId]}") private Long entityId;

	@Resource private ClientExchangeTransferRepository clientExchangeTransferRepository;
	
	private List<ClientExchangeTransfer> list = new ArrayList<>();
	
	@Override
	public ClientExchangeTransfer read()
			throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		
		if (list.size() == 0) {
			list = clientExchangeTransferRepository.findUnprocessed(entityId, DEFAULT_UNPROCESSED_SIZE);
		}
		
		if (list.size() == 0) return null; // if still empty end of processing
		
		ClientExchangeTransfer clientExchangeTransfer = list.get(0);
		list.remove(0);
		
		return clientExchangeTransfer;
	}

}
