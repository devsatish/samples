package com.satish.spectrum.vo;

import java.io.Serializable;

@SuppressWarnings("serial")
public class ApxTxInfo implements Serializable {
/*
 "last_tx": {
      "time_utc": "2015-10-07T17:20:54Z",
      "tx": "9359a8b10892d8101e64f77fd26520a5f55dfaba3b49407a9f7761916a665a48",
      "block_nb": "377884",
      "value": -29655,
      "confirmations": 28113
    },
 */
	
	public String time_utc, tx, block_nb;
	public double value;
	public int confirmations;
	
	public ApxTxInfo(){}
	
	public long getVolumeOut(){
		return MlUtils.btcToSatoshi(this.value);
	}
	
	public boolean isSent(){
		return value > 0;
	}
	
	public boolean getReceived(){
		return !isSent();
	}
	
	public boolean getConfirmed(){
		return confirmations > 2;
	}
}
