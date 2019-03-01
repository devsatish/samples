package com.satish.spectrum.vo;

import java.io.Serializable;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseEntityLabel extends ResponseEntity<ApiLabeledAddress> implements Serializable {

	public ResponseEntityLabel(HttpStatus statusCode) {
		super(statusCode);
		// TODO Auto-generated constructor stub
	}

	
	
	
}
