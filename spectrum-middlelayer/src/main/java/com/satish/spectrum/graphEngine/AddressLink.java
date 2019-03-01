package com.satish.spectrum.graphEngine;

import java.io.Serializable;

import com.satish.spectrum.vo.ApiTxInput;
import com.satish.spectrum.vo.MlUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AddressLink implements Serializable{
	private ApiTxInput apiInput;
	public String hash;
	public String from; 
	public String to;
	public Long value;
	public String label;
	public String labelType;
	public String url;
	
	
	
	public AddressLink(String from, String to, ApiTxInput apiInput){
		this.from = from;
		this.to = to;
		this.apiInput = apiInput;
		this.hash = (apiInput != null && apiInput.address != null) ? apiInput.address.hash : "-";
		this.value = apiInput != null ? apiInput.value : 0;	
		this.label = "";
		this.labelType = "";
		this.url = "";
	}
	
	public String asJson(){
		ObjectMapper mapper = new ObjectMapper();
		String objJson = "";
		
		try {
			 objJson = mapper.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			 e.printStackTrace();
		}
		
		return objJson;
	}
	
	@Override
	public String toString(){
		String s = "AdrLink (" + MlUtils.shorten(from) + " => " + MlUtils.shorten(to) + ") ";
		s += MlUtils.shorten(hash) + "/" + MlUtils.toBtcString(value);
		return s;
	}
}
