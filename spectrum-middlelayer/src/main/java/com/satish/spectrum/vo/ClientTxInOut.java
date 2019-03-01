package com.satish.spectrum.vo;

import java.io.Serializable;

import com.satish.spectrum.db.Label;

public class ClientTxInOut extends ClientLabel implements Comparable<ClientTxInOut>, Serializable{
	
	public String address;
	public int count;
	public long value;
	public Risk risk;
	public String clusterId;
	public String nextTx;
	public String prevTx;
	
	
	public ClientTxInOut(String address, long value, String cls, String prevTx, String nextTx){
		count = 1;
		
		risk = new Risk();
		this.address = address;
		this.value = value;
		this.clusterId = cls;
		this.prevTx = prevTx;
		this.nextTx = nextTx;
	}
	
	@Override
	public int compareTo(ClientTxInOut other) {
		return Long.compare(other.value, this.value); // descending 
	}
	
}