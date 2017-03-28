package uk.ac.ljmu.fet.cs.group20;

import hu.mta.sztaki.lpds.cloud.simulator.Timed;

public class MyFirstDCFCode {

	public static void main(String[] args) {
		
		PeriodicWriter pW = new PeriodicWriter();
		WriteOnce wO = new WriteOnce(pW);
		
		pW.setMarker("");
		wO.setPayload("Hello World");
		
		Timed.simulateUntil(4);

	}

}
