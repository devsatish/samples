package com.satish.spectrum.graphEngine;

import java.io.Serializable;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.satish.spectrum.vo.ApiTransaction;
import com.satish.spectrum.vo.ApiTxInput;
import com.satish.spectrum.vo.ClientTransaction;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
@Scope("prototype")
public class TxNode implements Comparable<TxNode>, Serializable{
	
	public String hash;
	private ApiTransaction aTx;
	public ClientTransaction cTx;
	public int depth = 0;
	public long value;
	
	public TxNode(String hash, int depth, long value){
		this.hash = hash;
		this.depth = depth;
		this.value = value;
	}
	
//	public ApiTransaction getApiTransaction(){
//		return aTx;
//	}
	
	public List<ApiTxInput> getRelevantInputs(long minVolume){
		return aTx.allInputsBiggerThan(minVolume);
	}
	
	/*
	public void updateLabels(LabelProvider provider, long minValue, boolean inputsOnly) throws Exception{
		cTx.updateAllLabels(provider, minValue, inputsOnly);
	}
	*/
	
	public TxNode(ApiTransaction apiTx, int depth){
		this(apiTx.hash, depth, apiTx.volumeOut);
		aTx = apiTx;
		cTx = apiTx.asClientTransaction();
	}
	
	public double reducer(){
		return aTx.reducer;
	}
	
	public boolean isUptodate() {
		return aTx != null;
	}
	
	public ClientTransaction clientTx(){
		return cTx;
	}
	
	public String asJson(){
		ObjectMapper mapper = new ObjectMapper();
		String objJson = "";
		
		try {
			 objJson = mapper.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return objJson;
	}
	
	@Override
	public String toString(){
		String indent = "";
		for(int i=0; i < depth; i++) indent += "\t";
		return indent + aTx + "\n";
	}

	@Override
	public int compareTo(TxNode o) {
		return depth - o.depth;
	}
}