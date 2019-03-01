package com.satish.spectrum.db;

import java.sql.Date;
import java.util.Arrays;

import org.bitcoinj.core.AddressFormatException;
import org.bitcoinj.core.Base58;
import org.bitcoinj.core.Sha256Hash;



public class AddressLabel {
	
	
	
	private byte[] address;
	private int address_version;
	private String type;
	private String label;
	private byte[] url_hash;
	private String url;
	private String source;
	private Date documented_at;
	private Date created_at;
	
	public AddressLabel() {}
	
	public static byte[] get20bytesFromAddress(String address) throws Exception {
		byte[] b21 = Base58.decodeChecked(address);
		byte[] revertedAdress = new byte[20]; 
		System.arraycopy(b21, 1, revertedAdress, 0, b21.length-1);
		return revertedAdress;
	}
	
	public static String getAddressFrom20bytes(byte[] input) {
			if (input == null) return "";
			
			byte[] b21 = new byte[21];
			b21[0] = 0x00;
			byte[] b20 = input;
			System.arraycopy(b20, 0, b21, 1, b20.length);
			byte[] checksum =  Sha256Hash.hashTwice(b21);
			byte[] b25 = new byte[25];
			
			System.arraycopy(b21, 0, b25, 0, b21.length);
			System.arraycopy(checksum, 0, b25, b21.length, 4);
			String bAddress = Base58.encode(b25);
			
			return bAddress;
		
	}
	
	public byte[] getAddress() {
		return address;
	}
	public void setAddress(byte[] address) {
		this.address = address;
	}
	public int getAddress_version() {
		return address_version;
	}
	public void setAddress_version(int address_version) {
		this.address_version = address_version;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public byte[] getUrl_hash() {
		return url_hash;
	}
	public void setUrl_hash(byte[] url_hash) {
		this.url_hash = url_hash;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public Date getDocumented_at() {
		return documented_at;
	}
	public void setDocumented_at(Date documented_at) {
		this.documented_at = documented_at;
	}
	public Date getCreated_at() {
		return created_at;
	}
	public void setCreated_at(Date created_at) {
		this.created_at = created_at;
	}
	
	
	
	
}
