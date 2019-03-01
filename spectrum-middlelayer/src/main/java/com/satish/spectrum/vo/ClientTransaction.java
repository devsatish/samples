package com.satish.spectrum.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.satish.spectrum.db.Label;
import com.satish.spectrum.graphEngine.MiniHistogramEntry;
import com.satish.spectrum.service.DbService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


@SuppressWarnings("serial")
@Service
@Scope("prototype")
public class ClientTransaction implements Serializable{
	
	public static DbService DbSvc;
	
	public static final int INOUT_SIZE = 100;
	
	public boolean completed;
	public String hash;
	public long fee;
    public long volumeIn;
    public long volumeOut;
    public int numInputs;
    public int numOutputs;
    public boolean coinbase;
    public Risk risk; 
	public List<ClientTxInOut> inputs, outputs;
	public int timestamp;
	private Map<String, ClientLabel> labelMap;
	private Map<String, MiniHistogramEntry> hSendersMap = new HashMap<String, MiniHistogramEntry>();
	private Map<String, MiniHistogramEntry> hReceiversMap = new HashMap<String, MiniHistogramEntry>();
	private Map<String, MiniHistogramEntry> hLabelsMap = new HashMap<String, MiniHistogramEntry>();
	private Map<String, MiniHistogramEntry> hTypesMap = new HashMap<String, MiniHistogramEntry>();
	private double weight;
	
	public ClientTransaction(){}
	
	ClientTransaction(String hash, long fee, long volIn, long volOut, int numIns, int numOuts, int ts, ApiScore scores, boolean coinbase, double weight){
		this.completed = false;
    	this.hash = hash;
		this.fee = fee;
		this.volumeIn = volIn;
		this.volumeOut = volOut;
		this.coinbase = coinbase;
		this.timestamp = ts;
		this.weight = weight;
		
		risk = Risk.fromScores(scores);
	    
		this.numInputs = numIns;
		this.numOutputs = numOuts;
		inputs = new ArrayList<ClientTxInOut>();
		outputs = new ArrayList<ClientTxInOut>();
		labelMap = new HashMap<String, ClientLabel>();
		
	}
	
	void setOutputs(List<ApiTxOutput> rawOutputs, boolean trim){
		HashMap<String, ClientTxInOut> map = new HashMap<String, ClientTxInOut>();
		for(ApiTxOutput e: rawOutputs){
			if (e != null && e.address != null){
				if (!map.containsKey(e.address.hash)) {
					map.put(e.address.hash, new ClientTxInOut(e.address.hash, e.value, null, null, e.nextTransactionHash));
					hReceiversMap.put(e.address.hash, new MiniHistogramEntry(e.address.hash, e.value, 1));
				} else {
					ClientTxInOut o =  map.get(e.address.hash);
					o.count++;
					o.value += e.value;
					MiniHistogramEntry m = hReceiversMap.get(e.address.hash);
					m.value = o.value;
					m.ext = o.count; 
				}
				labelMap.putIfAbsent(e.address.hash, null);
			}
		}
		outputs.addAll(map.values());
		Collections.sort(outputs);
		
		if (trim && outputs.size() > INOUT_SIZE){
			List<ClientTxInOut> removed = new ArrayList<ClientTxInOut>();
			while(outputs.size() >= INOUT_SIZE) removed.add(outputs.remove(outputs.size()-1));
			ClientTxInOut last = new ClientTxInOut("others [ " + removed.size() + " ]", 0, null, null, null);
			last.count = removed.size();
			for (ClientTxInOut io : removed) {
				last.value += io.value;
				if (io.risk.source > last.risk.source) last.risk.source = io.risk.source;
				if (io.risk.counterpart > last.risk.counterpart) last.risk.counterpart = io.risk.counterpart;
				if (io.risk.behaviour > last.risk.behaviour) last.risk.behaviour = io.risk.behaviour;
			}
			last.risk.updateTotal();
			outputs.add(last);
		}
	}
	
	void setInputs(List<ApiTxInput> rawInputs, boolean trim){
		HashMap<String, ClientTxInOut> map = new HashMap<String, ClientTxInOut>();
		for(ApiTxInput e: rawInputs){
			if (e != null && e.address != null){
				if (!map.containsKey(e.address.hash)) {
					map.put(e.address.hash, new ClientTxInOut(e.address.hash, e.value, null, e.previousTransactionHash, null));
					hSendersMap.put(e.address.hash, new MiniHistogramEntry(e.address.hash, e.value, 1));
				} else {
					ClientTxInOut o =  map.get(e.address.hash);
					o.count++;
					o.value += e.value;
					MiniHistogramEntry m = hSendersMap.get(e.address.hash);
					m.value = o.value;
					m.ext = o.count; 
				}
				labelMap.putIfAbsent(e.address.hash, null);
			}
		}
		inputs.addAll(map.values());
		Collections.sort(inputs);
		
		if (trim && inputs.size() > INOUT_SIZE){
			List<ClientTxInOut> removed = new ArrayList<ClientTxInOut>();
			while(inputs.size() >= INOUT_SIZE) removed.add(inputs.remove(inputs.size()-1));
			ClientTxInOut last = new ClientTxInOut("others [ " + removed.size() + " ]", 0, null, null, null);
			last.count = removed.size();
			for (ClientTxInOut io : removed) {
				last.value += io.value;
				if (io.risk.source > last.risk.source) last.risk.source = io.risk.source;
				if (io.risk.counterpart > last.risk.counterpart) last.risk.counterpart = io.risk.counterpart;
				if (io.risk.behaviour > last.risk.behaviour) last.risk.behaviour = io.risk.behaviour;
			}
			last.risk.updateTotal();
			inputs.add(last);
		}
	}
	
	void setInOuts(List<ApiTxInput> rawInputs, List<ApiTxOutput> rawOutputs, boolean trim){
		hSendersMap = new HashMap<String, MiniHistogramEntry>();
		hReceiversMap = new HashMap<String, MiniHistogramEntry>();
		hLabelsMap = new HashMap<String, MiniHistogramEntry>();
		hTypesMap = new HashMap<String, MiniHistogramEntry>();
		setInputs(rawInputs, trim);
		setOutputs(rawOutputs, trim);
		try {
			updateLabelsFromDB();
			updateLabelHistos();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void updateLabelHistos(){
		for(ClientTxInOut i : inputs){
			if (i.hasLabel()){
				if (!hLabelsMap.containsKey(i.label)) {
					hLabelsMap.put(i.label, new MiniHistogramEntry(i.label, i.value, 1));
				} else {
					MiniHistogramEntry m = hLabelsMap.get(i.label);
					m.value += i.value;
					m.ext = (int) m.ext + 1; 
				}	
			}
			if (i.hasType()){
				if (!hTypesMap.containsKey(i.type)) {
					hTypesMap.put(i.label, new MiniHistogramEntry(i.type, i.value, 1));
				} else {
					MiniHistogramEntry m = hTypesMap.get(i.type);
					m.value += i.value;
					m.ext = (int) m.ext + 1; 
				}	
			}
		}
	}
	
	void updateLabelsFromDB() throws Exception {
		
		List<String> addresses = new ArrayList<String>();
		addresses.addAll(labelMap.keySet());
		
		if (DbSvc == null){
			throw new Exception("broken");
		}
		List<Label> labels = DbSvc.getLabelsForAddressHashes(addresses);

		for(Label label:labels) {
			labelMap.put(label.getAddressString(), label);
		}
		
		for(ClientTxInOut io:inputs) {
			ClientLabel lbl = labelMap.get(io.address);
			if(lbl != null) io.updateFromDbLbl(lbl);
		}
		
		for(ClientTxInOut io:outputs) {
			ClientLabel lbl = labelMap.get(io.address);
			if(lbl != null) io.updateFromDbLbl(lbl);
		}
	}
	
	public Map<String, ClientLabel> allLabels(){ return labelMap;}
	
	public Map<String, MiniHistogramEntry> histoLabels(){ return hLabelsMap; }
	public Map<String, MiniHistogramEntry> histoTypes(){ return hTypesMap; }
	public Map<String, MiniHistogramEntry> histoSenders(){ return hSendersMap; }
	public Map<String, MiniHistogramEntry> histoReceivers(){ return hReceiversMap; }
	
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
		return  (risk.total > 0.0 ? (String.format("R %d",(int)risk.total) + "! ") : "") + MlUtils.shorten(hash) + " (" + numInputs + "➔" + numOutputs +") " + MlUtils.toBtcString(volumeOut);
	}

	private ClientTxInOut inputByAdddressHash(String toFind){
		for(ClientTxInOut io : inputs){
			if (io.address == toFind) return io;
		}
		return null;
	}
}