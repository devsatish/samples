package tech.dynamo.processor.record.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping(value={"/records", "/v1/records"}, method=RequestMethod.GET)
public interface RecordProcessorController {

	@RequestMapping("/")
	BatchResult processAll();
	
	@RequestMapping("/entitites/{entityId}")
	BatchResult processForEntity(Long clientEntityId);
}
