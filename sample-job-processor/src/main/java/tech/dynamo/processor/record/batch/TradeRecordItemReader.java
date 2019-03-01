package tech.dynamo.processor.record.batch;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.beans.factory.annotation.Value;

import tech.dynamo.core.domain.ClientExchangeTrade;
import tech.dynamo.core.repository.ClientExchangeTradeRepository;

public class TradeRecordItemReader implements ItemReader<ClientExchangeTrade> {
	
	private static final int DEFAULT_UNPROCESSED_SIZE = 50000;

	@Value("#{jobParameters[entityId]}") private Long entityId;
	
	@Resource private ClientExchangeTradeRepository clientExchangeTradeRepository;
	
	private List<ClientExchangeTrade> list = new ArrayList<>();
	
	@Override
	public ClientExchangeTrade read()
			throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		
		if (list.size() == 0) {
			list = clientExchangeTradeRepository.findUnprocessed(entityId, DEFAULT_UNPROCESSED_SIZE);
		}
		
		if (list.size() == 0) return null; // if still empty end of processing
		
		ClientExchangeTrade clientExchangeTrade = list.get(0);
		list.remove(0);
		
		return clientExchangeTrade;
	}

}
