package uk.ac.ljmu.fet.cs.group20;

import hu.mta.sztaki.lpds.cloud.simulator.Timed;

public class PeriodicWriter extends Timed {
	
	private String marker = "";
	
	public PeriodicWriter(){
		subscribe(1);
		System.out.print(marker);
	}

	@Override
	public void tick(long fires) {
		System.out.print(marker);
	}
	
	public void setMarker(String Marker){
		this.marker = Marker;
	}

}
