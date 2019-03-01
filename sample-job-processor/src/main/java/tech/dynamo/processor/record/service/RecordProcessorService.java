package tech.dynamo.processor.record.service;

import org.springframework.stereotype.Service;

@Service
public interface RecordProcessorService {
	
	Long processAll();

	Long processForEntity(Long entityId);
}
