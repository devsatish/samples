package com.satish.spectrum.service;

import java.io.IOException;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.satish.spectrum.vo.ApxAddressInfo;
import com.satish.spectrum.vo.ApxWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ExtraService {
	static final String BLOCKR_PREFIX = "http://btc.blockr.io/api/v1/address/info/";
	static final String BLOCKR_POSTFIX = "?confirmation=0";
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final ObjectMapper mapper = new ObjectMapper();

	ExtraService(){}
	
	public Future<ApxAddressInfo> getAddressInfo(String hash){
		
		ApxAddressInfo info = null;
		RestTemplate rt = new RestTemplate();
		String url = BLOCKR_PREFIX + hash + BLOCKR_POSTFIX;
		ResponseEntity<String> response = rt.getForEntity(url, String.class);
		if (response.getStatusCode() == HttpStatus.OK){
	        // System.out.println(response);
	        try {
	        	info = mapper.readValue(response.getBody(), ApxWrapper.class).data;
			} catch (IOException e) {
				e.printStackTrace();
				logger.error("Failed <ApxAddressInfo>CONVERSION of " + hash);
			}
	    } else {
	        logger.error("Failed to GET info of " + hash);
	    }
		return new AsyncResult<ApxAddressInfo>(info);
	} 
}
