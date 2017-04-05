package uk.ac.ljmu.fet.cs.group20.testsolution;

import hu.mta.sztaki.lpds.cloud.simulator.DeferredEvent;
import hu.mta.sztaki.lpds.cloud.simulator.energy.MonitorConsumption;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.IaaSService;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.PhysicalMachine;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.constraints.ResourceConstraints;
import hu.unimiskolc.iit.distsys.forwarders.IaaSForwarder;
import hu.unimiskolc.iit.distsys.interfaces.CloudProvider;

public class LoadDependentProvider implements CloudProvider {
	
	IaaSService iaas;
	MonitorConsumption[] Monitors;

	@Override
	public double getPerTickQuote(ResourceConstraints rc) {
		double basePrice = 0.00005;
		double totalPrice = 0;
		
		//If no monitor is running or subscribe , we just return base price.
		if (Monitors == null || !Monitors[0].isSubscribed()) {
			return basePrice;
		}
		double currentTotalConsumption = 0;
		//Loop which goes through all active monitors and records all process done in an hour.
		for (MonitorConsumption mon : Monitors) {
			currentTotalConsumption += mon.getSubHourProcessing();
		}
		
		return totalPrice*(currentTotalConsumption/rc.getTotalProcessingPower());
	}

	@Override
	public void setIaaSService(IaaSService iaas) {
		this.iaas = iaas;
		((IaaSForwarder) iaas).setQuoteProvider(this);
		new DeferredEvent(2 * 24 * 60 * 60 * 1000 + 1) {
			@Override
			protected void eventAction() {
				Monitors = new MonitorConsumption[iaas.machines.size()];
				int i = 0;
				for (PhysicalMachine pm : iaas.machines) {
					Monitors[i++] = new MonitorConsumption(pm, 1000);
				}
				new DeferredEvent(102 * 10 * 60 * 1000l) {
					@Override
					protected void eventAction() {
						for (MonitorConsumption M : Monitors) {
							M.cancelMonitoring();
						}
					}
				};
			}
		};
	}

}
