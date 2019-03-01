package com.satish.spectrum.vo;

import java.io.Serializable;

public class ApiLabelType implements Comparable<ApiLabelType>, Serializable {
										// {
	public String type; 				// 	"type": "Advertisement Service",
	public String numLabeledAddresses; 	// 	"numLabeledAddresses": 2870,
	public String numClusters; 			// 	"numClusters": 57
										// }
	
	public ApiLabelType(){}
	
	public String toString(){
		return type + " (a:" + numLabeledAddresses + " c:" + numClusters + ")";
	}

	@Override
	public int compareTo(ApiLabelType other) {
		return this.type.compareTo(other.type);
	}
}
