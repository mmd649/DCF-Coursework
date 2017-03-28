package uk.ac.ljmu.fet.cs.group20;

import hu.mta.sztaki.lpds.cloud.simulator.DeferredEvent;

public class WriteOnce extends DeferredEvent {
	
	private String payload;
	private PeriodicWriter periodic;

	public WriteOnce(PeriodicWriter p) {
		super(2);
		this.periodic = p;
	}

	@Override
	protected void eventAction() {
		System.out.print(payload + " from DISSECT-CF");
		periodic.setMarker("!");
	}
	
	public void setPayload(String payload){
		this.payload = payload;
	}

}
