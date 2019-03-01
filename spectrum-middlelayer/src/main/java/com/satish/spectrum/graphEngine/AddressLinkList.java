package com.satish.spectrum.graphEngine;

import java.io.Serializable;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class AddressLinkList extends ArrayList<AddressLink> implements Serializable {

	public AddressLink getLinkBySource(String hash) {
		for(AddressLink al : this){
			if (al.from == hash) 
				return al;
		}
		return null;
	}

	public AddressLink getLinkByTarget(String hash) {
		for(AddressLink al : this){
			if (al.to == hash) 
				return al;
		}
		return null;
	}
}
