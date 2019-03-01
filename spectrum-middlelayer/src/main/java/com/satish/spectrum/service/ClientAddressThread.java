package com.satish.spectrum.service;

import com.satish.spectrum.vo.ClientAddress;

public class ClientAddressThread implements Runnable {
	ClientAddress a;

	public ClientAddressThread(ClientAddress a) {
		this.a = a;
	}

	@Override
	public void run() {

	}

}
