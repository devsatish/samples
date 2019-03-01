	package com.satish.spectrum.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.satish.spectrum.service.DbService;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@SuppressWarnings("serial")
@Component
@Scope("prototype")
@JsonIgnoreProperties
public class ApiTransaction implements Serializable {

	public String hash; 	// "hash": "ae01eeaa69714b1c178eeba6d4d90793666b8937d460685be19bfbc83e743091",
    public String version; 	// "version": 1,
    public int size; 		// "size": 259,
    public long fee; 		// "fee": 0,
    public long volumeIn; 	// "volumeIn": 36849750000,
    public long volumeOut; 	// "volumeOut": 36849750000,
    public int numInputs;  	// "numInputs": 1,
    public int numOutputs;	// "numOutputs": 2,
	public boolean coinbase = false;
    public List<ApiTxInput> inputs;
	public List<ApiTxOutput> outputs;
	public String blockHash; 	// "blockHash": "000000000000002e...db5b4",
    public int blockHeight; 	// "blockHeight": 255397,
    public int blockPosition; 	// "blockPosition": 2,
    public int blockTimeEpochSecond; // "blockTimeEpochSecond": 1378002748
	public ApiScore scores; // "scores": {"coinflow: 50}
    public double reducer = 1.0;

	public ApiTransaction() {
		reducer = 1.0;
	}
	
	public List<ApiTxInput> allInputsBiggerThan(long minValue){
		Collections.sort(inputs);
		
		//get inputs where p.value*reducer >= minvalue
		
		List<ApiTxInput> list  = new ArrayList<ApiTxInput>();
		
		for(ApiTxInput ati:inputs) {
			if(ati.value*reducer >= minValue) {
				list.add(ati);
			}
		}
		
		int delta = inputs.size() - list.size();
		if (delta > 0) System.out.print("(" + inputs.size() + "-"+ delta+")");
		return list;
	}
	
	public ApiTxOutput getOutput(long previousIndex){
		for(ApiTxOutput o : outputs){
			if (o.index == previousIndex) return o;
		}
		return null;
	}
	
	@Override
	public String toString(){
		String v = MlUtils.toBtcString(volumeOut);
		return "Tx<" + MlUtils.shorten(hash) + "> [" + inputs.size() + "/" + outputs.size() + String.format("] r: %.3f ",reducer) + v;
	}
	
	public ClientTransaction asClientTransaction(){
		ClientTransaction cTx = new ClientTransaction(hash, fee, volumeIn, volumeOut, numInputs, numOutputs, blockTimeEpochSecond, scores, coinbase, reducer);
		cTx.setInOuts(inputs, outputs, true);
		return cTx;
	}
	
	public String asJsonClientTransaction(){
		return asClientTransaction().asJson();
	}

	public String getOutputsClusterIdOf(String addressHash) {
		for(ApiTxOutput ati:outputs) {
			if(ati.address.hash.equals(addressHash)) {
				return ati.address.clusterId;
			}
		}
		
		return "";
	}
}

