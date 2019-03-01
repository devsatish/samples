package tech.dynamo.processor.record.batch;

import java.math.BigDecimal;

import org.springframework.batch.item.ItemProcessor;

import tech.dynamo.core.domain.ClientExchangeTransfer;

/**
 * The class filter zero fee amounts out of processing
 * @author zalexs
 *
 */
public class TransferRecordItemProcessor implements ItemProcessor<ClientExchangeTransfer, ClientExchangeTransfer> {

	@Override
	public ClientExchangeTransfer process(ClientExchangeTransfer item) throws Exception {
		
		if (item.getFeeAmount() == null || BigDecimal.ZERO.compareTo(item.getFeeAmount()) == 0) return null;
		
		return item;
	}

}
