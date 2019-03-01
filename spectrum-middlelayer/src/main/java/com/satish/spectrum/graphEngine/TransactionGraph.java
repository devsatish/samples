package com.satish.spectrum.graphEngine;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.satish.spectrum.vo.ApiTransaction;
import com.satish.spectrum.vo.ApiTxInput;
import com.satish.spectrum.vo.ClientLabel;
import com.satish.spectrum.vo.ClientTransaction;
import com.satish.spectrum.vo.MlUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@SuppressWarnings("serial")
public class TransactionGraph implements Serializable{
	
	public int getDepth() {
			return nodes.deepestNodeDepth();
	}

	private TxNode root;
	private TxNodeList nodes;
	private AddressLinkList links;
	public boolean completed;
	public String rootHash;
	public D3Graph graph;
	public Map<String, ClientTransaction> txMap;
	// public Map<String, ClientLabel> lblMap;
	public List<MiniHistogram> histograms;
	public String message = "";
	
	public TransactionGraph(ApiTransaction rootTransaction) {
		completed = false;
		nodes = new TxNodeList();
		links = new AddressLinkList();
		root = new TxNode(rootTransaction, 0);
		rootHash = root.hash;
		nodes.add(root);
		// _graph = new DefaultDirectedGraph<String, DefaultEdge>(DefaultEdge.class);
	}
	
	public void addLink(String from, String to,ApiTxInput ai){
		// _graph.addEdge(from, to);
		links.add(new AddressLink(from, to, ai));
	}
	
	public void addNode(TxNode node){ 
		nodes.add(node);
	}
	
	public TxNodeList nodesAtDepth(int d){
		return nodes.atDepth(d);
	}
	
	public int numNodes(){
		return nodes.size();
	}
	
	public int numLinks(){
		return links.size();
	}
	
	public void updateAll(boolean deeper){
		if (deeper) { nodes.updateHistograms(); }
		txMap = nodes.transactionsMap();
		histograms = nodes.histograms();
		graph = new D3Graph(nodes, links);
		completed = true;
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
	
	public String info(){ 
    	String s = "TransactionGraph: " + this;
    	s += "\n\tNodes: \n" + this.nodes.toString();
    	s += "\n\tLinks: \n" + this.links.toString();
    	return s;
    }
	
	@SuppressWarnings("unused")
	private String dbgInfo(){
		String s = "TG[d: " + this.getDepth() + "] (n/l: "+this.nodes.size()+"/"+this.links.size()+")";
		s+="d3(n/l): "+this.graph.nodes.size() + "/"+ this.graph.links.size();
		return s;
	}
	
    @Override
    public String toString(){
    	String s = "TransactionGraph of <" + MlUtils.shorten(rootHash) + "> ";
    	s += "[nodes: " + nodes.size() + " ";
    	s += "links: " + links.size() + "]";
    	return s;
    }
    
}
