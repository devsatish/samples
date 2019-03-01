package com.satish.spectrum.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.satish.spectrum.db.AddressLabel;
import com.satish.spectrum.db.Label;

@SuppressWarnings("serial")
public class ClientAddress implements Serializable{
	public String hash = "",
		clusterId = "",
		label = "", 
		url = "",
		category = "",
		firstSeen = "",
		lastSeen = "";
	public Risk risk = new Risk();
	public List<TxVolumeInfo> sentTransactions = new ArrayList<TxVolumeInfo>();
	public List<TxVolumeInfo> receivedTransactions = new ArrayList<TxVolumeInfo>();
	public long received = 0, sent = 0, balance = 0, balanceMsig = 0;
	public int numTransactions = 0;
	public ApxTxInfo firstTx, lastTx;
	
	public ClientAddress(){}
	
	public ClientAddress(String hash){
		this.hash = hash;
	}
	
	public void updateLabel(Label lbl) {
		if (lbl != null){
			label = lbl.label;
			category = lbl.type;
			url = lbl.url;
			if (clusterId.isEmpty() && lbl.cluster_id != null){
				clusterId = AddressLabel.getAddressFrom20bytes(lbl.cluster_id);
			}
		}
	}
	
	public void updateTop100(List<TxVolumeInfo> txs, boolean sent){
		for(TxVolumeInfo tv : txs){
			if (sent) {
				sentTransactions.add(tv);
			} else {
				receivedTransactions.add(tv);
			}
		}
	}
	
	public void updateRisk(List<TxScoresInfo> txs){
		for(TxScoresInfo tsi : txs){
			ClientTxInfo ti = ClientTxInfo.fromApiTx(tsi);
			if (ti.risk.total > risk.total) risk.total = ti.risk.total;
			if (ti.risk.behaviour > risk.behaviour) risk.behaviour = ti.risk.behaviour;
			if (ti.risk.counterpart > risk.counterpart) risk.counterpart = ti.risk.counterpart;
			if (ti.risk.source > risk.source) risk.source = ti.risk.source;
		}
	}
	
	@Override
	public String toString(){
		String s = "<" + hash +  "> ";
		if (!label.isEmpty()){ s += label + "(" + category +") "; }
		s += "b: " + MlUtils.toBtcString(balance);
		s += " nTxs: " + numTransactions;
		return s;
	}

	public void updateInfo(ApxAddressInfo info) {
		balance = MlUtils.btcToSatoshi(info.balance);
		balanceMsig = MlUtils.btcToSatoshi(info.balance_multisig);
		received = MlUtils.btcToSatoshi(info.totalreceived);
		sent = MlUtils.btcToSatoshi(info.totalreceived - info.balance);
		numTransactions = info.nb_txs;
		firstTx = info.first_tx;
		lastTx = info.last_tx;
		firstSeen = info.first_tx.time_utc;
		lastSeen = info.last_tx.time_utc;
		//System.out.println("ClientAddress.updateInfo() " + this.toString());
	}
}
