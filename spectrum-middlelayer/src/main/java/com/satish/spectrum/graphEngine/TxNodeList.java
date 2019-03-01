package com.satish.spectrum.graphEngine;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.satish.spectrum.vo.ClientLabel;
import com.satish.spectrum.vo.ClientTransaction;

@SuppressWarnings("serial")
public class TxNodeList extends ArrayList<TxNode> implements Serializable{

	public Map<String, ClientLabel> labelMap(){ return lblMap; }
	public Map<String, ClientTransaction> transactionsMap(){ return ctxMap; }
	public List<MiniHistogram> histograms() { return histos; }
	
	private Map<String, ClientLabel> lblMap = new HashMap<String, ClientLabel>();
	private Map<String, ClientTransaction> ctxMap = new HashMap<String, ClientTransaction>();
	private Map<String, MiniHistogramEntry> histoSendersMap = new HashMap<String, MiniHistogramEntry>();
	private Map<String, MiniHistogramEntry> histoReceiversMap = new HashMap<String, MiniHistogramEntry>();
	private Map<String, MiniHistogramEntry> histoLabelsMap = new HashMap<String, MiniHistogramEntry>();
	private Map<String, MiniHistogramEntry> histoTypesMap = new HashMap<String, MiniHistogramEntry>();
	private List<MiniHistogram> histos = new ArrayList<MiniHistogram>();
	private int maxDepth = 0;
	
	@Override
	public boolean add(TxNode n){
		boolean added = n != null && this.getByHash(n.hash) == null && super.add(n);
		if (added && n.depth > maxDepth) maxDepth = n.depth;
		return added;
	}
	 
	public int deepestNodeDepth() { return maxDepth; }
	
	public TxNodeList atDepth(int depth){
		TxNodeList list = new TxNodeList();
		for(TxNode x : this)
			if(x.depth == depth) list.add(x);
		return list;
	}
	
	public TxNode getByHash(String hash){
		TxNode found = null;
		for(TxNode e : this){
			if (e.hash == hash) {
				found = e;
				break;
			}
		}
		return found;
	}
	
	public String toString(){
		Collections.sort(this);
		String s = "";
		for(TxNode x : this)
			s += x.toString();
		return s;
	}
	
	public void updateHistograms(){
		histos = new ArrayList<MiniHistogram>();
		MiniHistogram hTxs = new MiniHistogram("largest transactions", "risk");
		MiniHistogram hSenders = new MiniHistogram("largest senders", "count");
		MiniHistogram hReceivers = new MiniHistogram("largest receivers", "count");
		MiniHistogram hLabels = new MiniHistogram("most seen labels", "count");
		MiniHistogram hTypes = new MiniHistogram("most seen categories", "count");
		//MiniHistogram hRisks = new MiniHistogram("risky transactions", false);
		
		for(TxNode n : this){
			ClientTransaction ctx = n.clientTx();
			// ctx.updateRisks(riskyLabelTypes);
			ctxMap.put(ctx.hash, ctx);
			lblMap.putAll(ctx.allLabels());
			
			hTxs.data.add(new MiniHistogramEntry(ctx.hash, ctx.volumeOut, ctx.risk.total));
			addHistoMapValues(histoLabelsMap, ctx.histoLabels());
			addHistoMapValues(histoSendersMap, ctx.histoSenders());
			addHistoMapValues(histoReceiversMap, ctx.histoReceivers());
			addHistoMapValues(histoTypesMap, ctx.histoTypes());
		}
		
		hSenders.data.addAll(histoSendersMap.values());
		hReceivers.data.addAll(histoReceiversMap.values());
		hLabels.data.addAll(histoLabelsMap.values());
		hTypes.data.addAll(histoTypesMap.values());
		
		Collections.sort(hTxs.data);
		Collections.sort(hSenders.data);
		Collections.sort(hReceivers.data);
		Collections.sort(hLabels.data);
		Collections.sort(hTypes.data);
		
		
		histos.add(hTxs);
		histos.add(hSenders);
		histos.add(hReceivers);
		histos.add(hLabels);
		histos.add(hTypes);
		for(MiniHistogram mh : histos){
			for(int i = mh.data.size()-1; i >= 10; i--){
				mh.data.remove(i);
			}
		}
	}
	
	private void addHistoMapValues(Map<String, MiniHistogramEntry> toUpdate, Map<String, MiniHistogramEntry> toAdd){
		for (Map.Entry<String, MiniHistogramEntry> entry : toAdd.entrySet()){
			if (toUpdate.containsKey(entry.getKey())){
				MiniHistogramEntry m = toUpdate.get(entry.getKey());
				MiniHistogramEntry a = entry.getValue();
				m.value += a.value;
				if (m.ext instanceof Integer) m.ext = (int)m.ext + (int)a.ext;
			}else{
				toUpdate.put(entry.getKey(), entry.getValue());
			}
		}
	}
}
