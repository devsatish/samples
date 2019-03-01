package tech.dynamo.processor.record.controller;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import tech.dynamo.processor.record.service.RecordProcessorService;

@RestController
public class RecordProcessorControllerImpl implements RecordProcessorController {

	@Resource private RecordProcessorService recordProcessorService;
	
	@Override
	public BatchResult processAll() {

		return new BatchResult(recordProcessorService.processAll());
	}

	@Override
	public BatchResult processForEntity(@PathVariable Long entityId) {
		
		return new BatchResult(recordProcessorService.processForEntity(entityId));
	}

}
