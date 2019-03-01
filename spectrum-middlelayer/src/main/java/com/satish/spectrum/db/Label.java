package com.satish.spectrum.db;

import java.sql.Date;

import com.satish.spectrum.vo.ClientLabel;

public class Label extends ClientLabel {
	
	public byte[] address;
	public byte[] cluster_id;

	
	public String getAddressString() {
		return this.address == null ? "" : AddressLabel.getAddressFrom20bytes(this.address);
	}
	
	public void setAddressString(String addressString) {
		try{
			this.address = addressString == "" ? null : AddressLabel.get20bytesFromAddress(addressString);
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public String getClusterString() {
		return this.cluster_id == null ? "" : AddressLabel.getAddressFrom20bytes(this.cluster_id);
	}
	
	public void setClusterString(String clusterString) {
		try{
			this.cluster_id = clusterString == "" ? null :AddressLabel.get20bytesFromAddress(clusterString);
		} catch(Exception e){
			e.printStackTrace();
		}
	}
}
