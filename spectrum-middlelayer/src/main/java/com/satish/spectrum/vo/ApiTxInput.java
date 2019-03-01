package com.satish.spectrum.vo;

import java.io.Serializable;

public class ApiTxInput extends ApiTxIo implements Serializable, Comparable<ApiTxInput> {
	
	public String previousTransactionHash; // "previousTransactionHash": "02d7b4b8d64ebf676b960fb4e7be8080d5b8944ce0d57d9e5f77c84387a35e83",
	public long previousIndex; // "previousIndex": 0,
	public long value;  //"value": 36849750000,
	public ApiAddress address; //"address": {"hash": "136jvwH7Rj99TaZPm6cuGgLyutmTX1p4Q4"},
	public String scriptSigHex; // "scriptSigHex": "493046022100aa09790347fa49acce4a840bbf25ac2e04984837147b327b0048a222c105e36c022100d4425b3e64a8a42e2f3e92308fe6c0c37da081131778ddd10eb0ce2f03d822af01410458a32287961c3da540e1ab5a09d94ec567fc1e77f8b7e44040920dd988227a89b5aa2c181a7bb0b626fb9086549e2f53c4a6bd9b8e9d8cd0cbe547fd8cf2ab48",
	public String scriptPubKeyHex; // "scriptPubKeyHex": "76a91417062adfaff50661f6d5d2b0d5eed9258a4da9af88ac",
    public String type; // "type": "PUBKEYHASH",
    public String sigHashType; //  "sigHashType": "ALL"
    
    public ApiTxInput(){}

	@Override
	public int compareTo(ApiTxInput o) {
		// descending order
		if (value > o.value) 
			return -1;
		else if (value < o.value)
			return 1;
		else 
			return 0;
	};

}
