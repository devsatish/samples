package com.satish.spectrum.vo;

import java.io.Serializable;

public class ApiTxOutput extends ApiTxIo implements Serializable{
	public int index; //"index": 0,
	public long value;  //"value": 49650000,
	public ApiAddress address; // "address": {"hash": "1Q3FseGXHkchsj3bmJn88fU6omiK5gA6PU"},
	public String scriptPubKeyHex; // "scriptPubKeyHex": "76a914fcb823f3213eef4661f123c695ef20dd2267f58b88ac",
	public String type; //"type": "PUBKEYHASH"
	public String nextTransactionHash = "";
	
	public ApiTxOutput(){}
}
