package com.satish.spectrum.controller;

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

import com.satish.spectrum.service.BlockstemService;
import com.satish.spectrum.service.DbService;
import com.satish.spectrum.vo.ApiTransaction;
import com.satish.spectrum.vo.ClientTransaction;
import com.satish.spectrum.vo.TxVolumeInfo;

@CrossOrigin
@Controller
@RequestMapping("/transaction")
public class TransactionController {

	// private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	BlockstemService blockstemService;

	@Autowired
	DbService dbservice;

	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody ClientTransaction fromHash(@RequestParam String txHash) {
		if (ClientTransaction.DbSvc == null) {
			ClientTransaction.DbSvc = dbservice;
		}
		ApiTransaction apitx = blockstemService.getApiTransaction(txHash);
		if (apitx != null) {
			return apitx.asClientTransaction();
		} else {
			throw new ResourceAccessException("Not Found");
		}

	}

	@ExceptionHandler(ResourceAccessException.class)
	@ResponseStatus(value = HttpStatus.NOT_FOUND)
	public @ResponseBody String handleException(ResourceAccessException e) {
		return null;
	}

	@RequestMapping(value = "/next", method = RequestMethod.GET)
	public @ResponseBody ClientTransaction nextTx(@RequestParam String hash, @RequestParam String address) {
		if (ClientTransaction.DbSvc == null) {
			ClientTransaction.DbSvc = dbservice;
		}
		ApiTransaction tx = blockstemService.getNextTxFromPrevTxandInputAddress(hash, address);
		if (tx != null)
			return tx.asClientTransaction();
		else
			throw new ResourceAccessException("Not Found");
	}

	@RequestMapping(value = "/byInput", method = RequestMethod.GET)
	public @ResponseBody ArrayList<TxVolumeInfo> txByInput(@RequestParam String inAddress) {

		ArrayList<TxVolumeInfo> result = blockstemService.getTxsByInOut(inAddress, null);
		if (result != null)
			return result;
		else
			throw new ResourceAccessException("Not Found");
	}

	@RequestMapping(value = "/byOutput", method = RequestMethod.GET)
	public @ResponseBody ArrayList<TxVolumeInfo> txByOutput(@RequestParam String outAddress) {
		ArrayList<TxVolumeInfo> result = blockstemService.getTxsByInOut(null, outAddress);
		if (result != null)
			return result;
		else
			throw new ResourceAccessException("Not Found");
	}

	@RequestMapping(value = "/byInOut", method = RequestMethod.GET)
	public @ResponseBody ArrayList<TxVolumeInfo> txByInOut(@RequestParam String inAddress, @RequestParam String outAddress) {
		ArrayList<TxVolumeInfo> result = blockstemService.getTxsByInOut(inAddress, outAddress);
		if (result != null)
			return result;
		else
			throw new ResourceAccessException("Not Found");
	}
}
