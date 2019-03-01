package com.satish.spectrum.graphEngine;

import java.io.Serializable;

import com.satish.spectrum.vo.MlUtils;

public class D3Link implements Serializable{
	public int source;
    public int target;
    public long value;
    public int risk;
    public String address;
    public String text;
 
    public D3Link(int sourceIndex, int targetIndex, long value, int risk, String address, String text){
    	source = sourceIndex;
    	target = targetIndex;
    	this.value = value;
    	this.risk = risk;
    	this.address = address;
    	this.text = text;
    }
}
