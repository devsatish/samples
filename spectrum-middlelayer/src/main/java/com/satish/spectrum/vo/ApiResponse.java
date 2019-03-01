package com.satish.spectrum.vo;

import java.io.Serializable;
import java.util.ArrayList;

public class ApiResponse<T> implements Serializable{
	
	public int page; 				//    "page": 1,
	public int resultsPerPage;		//	  "resultsPerPage": 1,
	public ArrayList<T> results;    // 	  "results": [jsonObjects]
	
	public ApiResponse(){}
}
