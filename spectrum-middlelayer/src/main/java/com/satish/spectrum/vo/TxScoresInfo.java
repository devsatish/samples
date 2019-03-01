package com.satish.spectrum.vo;

import java.io.Serializable;

@SuppressWarnings("serial")
public class TxScoresInfo implements Serializable {
	public String hash = "";
	public ApiScore scores = new ApiScore();
	
	public TxScoresInfo(){}
}
