package com.satish.spectrum.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.ResourceAccessException;

import com.satish.spectrum.service.AddressService;
import com.satish.spectrum.service.BlockstemService;
import com.satish.spectrum.service.DbService;
import com.satish.spectrum.vo.ClientAddress;
import com.satish.spectrum.vo.ClientTransaction;

@CrossOrigin
@Controller
@RequestMapping("/address")
public class AddressController {

	// private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	AddressService addressService;

	@Autowired
	DbService dbservice;

	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody ClientAddress fromHash(@RequestParam String hash) {
		if (ClientTransaction.DbSvc == null) {
			ClientTransaction.DbSvc = dbservice;
		}

		ClientAddress result = addressService.getAddress(hash);
		if (result != null)
			return result;
		else
			throw new ResourceAccessException("Not Found");

	}

	@ExceptionHandler(ResourceAccessException.class)
	@ResponseStatus(value = HttpStatus.NOT_FOUND)
	public @ResponseBody String handleException(ResourceAccessException e) {
		return null;
	}

}
