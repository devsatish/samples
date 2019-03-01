package com.satish.spectrum.vo;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ApiLabeledAddress extends ClientLabel implements Serializable{

	public String address;	// "address": "112BkY47vfLhW7CfpxJkTN7kAUJijzanke",
	 
	public ApiLabeledAddress(){}
	
	public  static ApiLabeledAddress  fromJson(String json) {
		
		ApiResponse<ApiLabeledAddress> apiResponse = null;
		TypeReference<ApiResponse<ApiLabeledAddress>> typeRefApiLabelAddress = new TypeReference<ApiResponse<ApiLabeledAddress>>() {
		};
		ObjectMapper mapper = new ObjectMapper();
		try {
			apiResponse = mapper.readValue(json, typeRefApiLabelAddress);
			if(apiResponse.results.size() >0 && apiResponse.results.get(0) !=null) {
				
				return apiResponse.results.get(0);
				
				
			}
			System.out.print(".");
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
		
		
	}
	
	public String toString(){
		String s = "label of \"" + address + "\": " + label + " [ " + type + " ]";
		if (url != null && !url.isEmpty()){
			s += " url: " + url;
		}
		return s;
	}
}
