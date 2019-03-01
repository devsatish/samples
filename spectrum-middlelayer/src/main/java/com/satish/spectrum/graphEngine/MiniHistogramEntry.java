package com.satish.spectrum.graphEngine;

import java.io.Serializable;

public class MiniHistogramEntry implements Serializable, Comparable<MiniHistogramEntry>{
	public String name;
	public long value;
	public Object ext;
	
	public MiniHistogramEntry(String name, long value, Object ext){
		this.name = name;
		this.value = value;
		this.ext = ext;
	}
	
	public MiniHistogramEntry(String name, long value){
		this(name, value, "");
	}
	
	@Override
	public int compareTo(MiniHistogramEntry o) {
		int result = 0;
		if (value > o.value)
			result--;
		else if (value < o.value)
			result++;
		else {
			result = name.compareTo(o.name);
		}
		return result;
	}
	
	
}
