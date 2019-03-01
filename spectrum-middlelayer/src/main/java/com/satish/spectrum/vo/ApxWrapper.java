package com.satish.spectrum.vo;

import java.io.Serializable;

@SuppressWarnings("serial")
public class ApxWrapper implements Serializable {
/*
 {
  "status": "success",
  "data": <ApxAddressInfo>
  "code": 200,
  "message": ""
}
 */
	public String status, message;
	public int code;
	public ApxAddressInfo data;
	
	public ApxWrapper(){}
}
