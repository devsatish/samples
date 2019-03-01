package com.satish.spectrum.vo;

import java.io.Serializable;

import com.satish.spectrum.db.Label;

public class ClientLabel implements Serializable{
	public String label;
	public String type;
	public String url;
	
	public ClientLabel(){
		this.type = "";
		this.label = "";
		this.url = "";
	}
	public ClientLabel(String label, String type, String url){
		this.type = type;
		this.label = label;
		this.url = url;
	}
	
	public void updateFromDbLbl(ClientLabel lbl){
		this.label = lbl.label;
		this.url = lbl.url;
		this.type = lbl.type;
	}
	
	public boolean hasLabel(){ return this.label != ""; }
	public boolean hasType(){ return this.type != ""; }
}
