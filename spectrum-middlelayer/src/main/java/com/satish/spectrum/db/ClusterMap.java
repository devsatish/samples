package com.satish.spectrum.db;



import java.io.Serializable;



public class ClusterMap implements Serializable{
	
	public ClusterMap(){}
	
	
	
	
	
	private Long id;
	
	
	private byte[] cluster;
	private String clusterString;
	
	
	
	public byte[] getcluster() {
		return cluster;
	}
	public void setcluster(byte[] cluster) {
		this.cluster = cluster;
	}
	public String getclusterString() {
		return clusterString;
	}
	public void setclusterString(String clusterString) {
		this.clusterString = clusterString;
	}
	

}
