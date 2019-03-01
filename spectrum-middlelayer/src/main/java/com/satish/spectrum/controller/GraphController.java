package com.satish.spectrum.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.support.TaskUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.ResourceAccessException;

import com.satish.spectrum.graphEngine.TransactionGraph;
import com.satish.spectrum.service.DbService;
import com.satish.spectrum.service.GraphEngineService;
import com.satish.spectrum.vo.ClientTransaction;
import com.satish.spectrum.vo.MlUtils;

@CrossOrigin
@Controller
@RequestMapping("/graph")
public class GraphController {
	
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	DbService dbservice;

	@Autowired
	GraphEngineService graphEngineService;
	
	@RequestMapping(method=RequestMethod.GET)
	public @ResponseBody TransactionGraph fromHash (@RequestParam String txHash, @RequestParam Integer depth) {
		if (ClientTransaction.DbSvc == null){ClientTransaction.DbSvc = dbservice; }
		if (depth == null) { depth = 5; }
		TransactionGraph g = null;
		try {
			logger.info("building graph of <" + MlUtils.shorten(txHash)+"> depth: " + depth);
			g = graphEngineService.buildGraph(txHash, depth);
		} catch (Exception e) {
			throw new ResourceAccessException("Not Found");
		}
		return g;
	}
	
	
	@ExceptionHandler(ResourceAccessException.class)
	@ResponseStatus(value = HttpStatus.NOT_FOUND)
	public @ResponseBody String handleException(ResourceAccessException e) {
		return null;
	}

}
