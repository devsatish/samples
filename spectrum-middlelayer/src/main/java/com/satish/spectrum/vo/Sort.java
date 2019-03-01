package com.satish.spectrum.vo;

import java.io.Serializable;

public class Sort implements Serializable{
	public String sortBy;
	public String sortDirection;
	
	public Sort(String by, String dir) {
		this.sortBy = by;
		this.sortDirection = dir.toUpperCase();
	}
	
	
	
	public String asJson() {
		String json = "{\"sortBy\": \""+ sortBy +"\",";
		json+= "\"sortDirection\": \""+ sortDirection +"\"}";
		
		return json;
	}
}
