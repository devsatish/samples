package com.satish.spectrum.graphEngine;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MiniHistogram implements Serializable{
	public String name;
	public List<MiniHistogramEntry> data;
	public String ext;
	
	public MiniHistogram(String name, String extType){
		ext = extType;
		this.name = name;
		data = new ArrayList<MiniHistogramEntry>();
	}
}
