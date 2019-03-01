package com.satish.spectrum.db;



public class AddressClusterId {
	
	
	private byte[] address;
	private byte[] cluster_id;
	
	
	public AddressClusterId(byte[] address, byte[] cluster_id) {
		this.address = address;
		this.cluster_id = cluster_id;
	}
	
	
	
	public AddressClusterId() {
		// TODO Auto-generated constructor stub
	}



	public byte[] getAddress() {
		return address;
	}
	public void setAddress(byte[] address) {
		this.address = address;
	}



	public byte[] getCluster_id() {
		return cluster_id;
	}



	public void setCluster_id(byte[] cluster_id) {
		this.cluster_id = cluster_id;
	}
	
	
	
}
