package com.satish.spectrum.vo;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Risk implements Serializable{
	
	private final static Logger logger = LoggerFactory.getLogger(Risk.class);
	
	public double source = 0, 
			behaviour = 0, 
			counterpart = 0,
			total = 0;
	
	public Risk(){}

	public void updateTotal() {
		total = Math.max(source, Math.max(behaviour, counterpart)); 
	}

	public static Risk fromScores(ApiScore scores) {
		Risk r = new Risk();
		try{
			r.behaviour = scores.coinflow;
			r.source = scores.coinsource;
			r.total = Math.max(r.behaviour, r.source);
		} catch(Exception e){
			logger.warn("scores was NULL, all risks are set to 0");
		}
		return  r;
	}

}
