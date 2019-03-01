package com.coinalytics.spectrum.database;

import java.sql.Blob;

public class BigLabelRow {
	
	
	public BigLabelRow(Blob cluster_id, Blob address, String clusterString, String addressString, String label, String type, String url) {
		this.cluster_id = cluster_id;
		this.address = address;
		this.clusterString = clusterString;
		this.addressString = addressString;
		this.label = label;
		this.type = type;
		this.url = url;
		
	}
	public Blob cluster_id ;
	public Blob address;
	public String clusterString; 
	public String addressString;
	public String label; 
	public String type; 
	public String url;
	
	
	

}
