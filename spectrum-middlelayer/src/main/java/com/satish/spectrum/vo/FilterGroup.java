package com.satish.spectrum.vo;

import java.io.Serializable;

public class FilterGroup implements Serializable {
	
	public String field;
	public String comparisonOperator;
	public String fieldValue;
	
	public FilterGroup(String field, String operator, String val) {
		this.field = field;
		this.comparisonOperator = operator;
		this.fieldValue = val;
	}
	
	public String asJson() {
		String json = "{\"field\": \""+ field +"\",";
		json+= "\"comparisonOperator\": \""+ comparisonOperator +"\",";
		json+= "\"fieldValue\": \""+ fieldValue +"\"}";
		return json;
	}
}