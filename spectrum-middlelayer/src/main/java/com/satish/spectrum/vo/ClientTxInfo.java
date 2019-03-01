package com.satish.spectrum.vo;

import java.io.Serializable;

@SuppressWarnings("serial")
public class ClientTxInfo implements Serializable {
	public String hash = "";
	public long fee = 0,
			volumeIn = 0, 
			volumeOut = 0;
	public boolean coinbase = false;
	public int ts = 0, 
			nIn = 0, 
			nOut = 0;
	public Risk risk;
	
	public ClientTxInfo(){}
	
	public static ClientTxInfo fromApiTx(ApiTransaction atx){
		ClientTxInfo i = new ClientTxInfo();
		i.hash = atx.hash;
		i.fee = atx.fee;
		i.volumeIn = atx.volumeIn;
		i.volumeOut = atx.volumeOut;
		i.coinbase = atx.coinbase;
		i.ts = atx.blockTimeEpochSecond;
		i.nIn = atx.numInputs;
		i.nOut = atx.numOutputs;
		i.risk = Risk.fromScores(atx.scores);
		return i;
	}

	public static ClientTxInfo fromApiTx(TxScoresInfo tsi) {
		ClientTxInfo i = new ClientTxInfo();
		i.hash = tsi.hash;
		i.risk = Risk.fromScores(tsi.scores);
		return i;
	}
}
