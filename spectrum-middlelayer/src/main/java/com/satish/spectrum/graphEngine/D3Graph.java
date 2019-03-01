package com.satish.spectrum.graphEngine;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.satish.spectrum.vo.ClientTransaction;
import com.satish.spectrum.vo.MlUtils;

public class D3Graph implements Serializable{

	public List<D3Node> nodes;
	public List<D3Link> links;
	
	public D3Graph(TxNodeList txNodes, AddressLinkList addressLinks){
		nodes = new ArrayList<D3Node>();
		links = new ArrayList<D3Link>(); 
		Map<String, Integer> nodeMap = new HashMap<String, Integer>(); 
		for (TxNode tn: txNodes){
			nodeMap.put(tn.hash, nodes.size());
			D3Node dn = new D3Node(tn);
			nodes.add(dn);
		}
		// add links 
		for (AddressLink al : addressLinks){
			if (al != null){
				if (nodeMap.containsKey(al.from) && nodeMap.containsKey(al.to)){
					int fromIndex = nodeMap.get(al.from);
					int toIndex = nodeMap.get(al.to);
					if (fromIndex >= 0 && toIndex >= 0) {
						TxNode txn = txNodes.getByHash(al.from);
						int r = 0;
						if (txn != null){
							ClientTransaction cTx = txn.cTx;
							r = cTx != null ?  (int)Math.round(txn.cTx.risk.total) : 0;
						}
						String text = (r == 0 ? MlUtils.shorten(al.hash) : al.labelType) + " " + MlUtils.toBtcString(al.value, false);
						D3Link l = new D3Link(fromIndex, toIndex, al.value, r, al.hash, text);	
						links.add(l);
					}
				}
			}
		}
	}
}
