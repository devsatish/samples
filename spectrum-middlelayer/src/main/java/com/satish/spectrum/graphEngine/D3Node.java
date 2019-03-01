package com.satish.spectrum.graphEngine;

import java.io.Serializable;

import com.satish.spectrum.vo.MlUtils;

public class D3Node implements Serializable{
    // var d = {name: n.hash, value: Math.pow(n.value / 100000000, 1/2)+1, risk: n.cTx.risk.total};
	public String name;
	public long value;
	public int risk;
	public String text;
	public int depth;
	public int timestamp;
	
	public D3Node(TxNode n){
		name = n.hash;
		value = n.value;
		risk = (int) Math.round(n.cTx.risk.total);
		text = n.cTx.toString();
		depth = n.depth;
		timestamp = n.cTx.timestamp;
	}
	
}
