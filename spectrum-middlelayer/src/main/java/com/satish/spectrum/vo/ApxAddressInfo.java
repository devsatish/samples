package com.satish.spectrum.vo;

import java.io.Serializable;

@SuppressWarnings("serial")
public class ApxAddressInfo implements Serializable{
/*
 "address": "3Hs6BYdun3t2A7eteDkBwnFN1rQX53rSYQ",
    "is_unknown": false,
    "balance": 0.20855,
    "balance_multisig": 0,
    "totalreceived": 29655.20855,
    "nb_txs": 20857,
    "first_tx": {
      "time_utc": "2015-04-10T20:03:00Z",
      "tx": "1fbf78e35d324b6877e3770b188174fd5597c7c301c151c8d0a69de9655f76da",
      "block_nb": "351573",
      "value": 29655,
      "confirmations": 54424
    },
    "last_tx": {
      "time_utc": "2015-10-07T17:20:54Z",
      "tx": "9359a8b10892d8101e64f77fd26520a5f55dfaba3b49407a9f7761916a665a48",
      "block_nb": "377884",
      "value": -29655,
      "confirmations": 28113
    },
    "is_valid": true
 */
	
	public String address;
	public boolean is_unknown, is_valid;
	public double balance, balance_multisig, totalreceived;
	public int nb_txs;
	public ApxTxInfo first_tx, last_tx;
	
	public ApxAddressInfo(){};
}
