package com.satish.spectrum.db;

import java.io.Serializable;

public class AddressMap implements Serializable{ // TODO: remove this class
	
	public AddressMap(){}
	private byte[] address;
	private String addressString;
	public byte[] getAddress() {
		return address;
	}
	public void setAddress(byte[] address) {
		this.address = address;
	}
	public String getAddressString() {
		return addressString;
	}
	public void setAddressString(String addressString) {
		this.addressString = addressString;
	}
	

}
