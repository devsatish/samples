package com.satish.spectrum.controller;

import java.io.IOException;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.ResourceAccessException;

import com.satish.spectrum.service.BigQService;
import com.satish.spectrum.service.BlockstemService;
import com.satish.spectrum.service.DbService;
import com.satish.spectrum.vo.ApiTransaction;
import com.satish.spectrum.vo.ClientTransaction;
import com.satish.spectrum.vo.TxVolumeInfo;

@CrossOrigin
@Controller
@RequestMapping("/bq")
public class BigQueryController {

	@Autowired
	BigQService bigQService;

	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody String addressForHash(@RequestParam String hash160)  throws IOException{

		String result = bigQService.getAddress(hash160);
		if (result != null) {
			return result;
		} else {
			throw new ResourceAccessException("Not Found");
		}

	}

	@ExceptionHandler(ResourceAccessException.class)
	@ResponseStatus(value = HttpStatus.NOT_FOUND)
	public @ResponseBody String handleException(ResourceAccessException e) {
		return null;
	}

}
