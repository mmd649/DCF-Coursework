package uk.ac.ljmu.fet.cs.CloudProviderCompetitors;

import java.util.List;

import org.apache.commons.lang3.RandomUtils;

import hu.mta.sztaki.lpds.cloud.simulator.iaas.IaaSService;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.PhysicalMachine;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.VMManager;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.VirtualMachine;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.constraints.ResourceConstraints;
import hu.unimiskolc.iit.distsys.interfaces.CloudProvider;
import hu.unimiskolc.iit.distsys.ExercisesBase;
import hu.unimiskolc.iit.distsys.forwarders.IaaSForwarder;
import hu.unimiskolc.iit.distsys.forwarders.IaaSForwarder.VMListener;

public class mud1 implements CloudProvider, VMManager.CapacityChangeEvent<PhysicalMachine>, VMListener {
	private IaaSService iaaSservice;
	private double price;
	private static int requestedVms = 0;
	private static double base = 1;
	
	@Override
	public double getPerTickQuote(ResourceConstraints rc) {
		// Price change based on the number of processors used
		double priceByProcessorCount = getChangedPriceByProcessorCount(rc.getRequiredCPUs());
		price = rc.getTotalProcessingPower() * priceByProcessorCount * 0.00000000005;
		
		// price = rc.getTotalProcessingPower() * 0.00000000005;  // With this value the test passes.
		
		return price;
	}

	@Override
	public void capacityChanged(ResourceConstraints newCapacity, List<PhysicalMachine> affectedCapacity) {
		final boolean newRegistration = iaaSservice.isRegisteredHost(affectedCapacity.get(0));
		if (!newRegistration) {
			try {
				for (PhysicalMachine pm : affectedCapacity) {
					// For every lost PM we buy a new one.
					iaaSservice.registerHost(ExercisesBase.getNewPhysicalMachine(RandomUtils.nextDouble(2, 5)));
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}	
	}

	@Override
	public void setIaaSService(IaaSService iaas) {
		// Set IaaS service
		iaaSservice = iaas;
		iaaSservice.subscribeToCapacityChanges(this);  
		((IaaSForwarder)iaaSservice).setQuoteProvider(this);  
		((IaaSForwarder)iaaSservice).setVMListener(this);
	}
	
	private double getChangedPriceByProcessorCount(double count) {
		return base * count;
	}

	@Override
	public void newVMadded(VirtualMachine[] vms) {
		requestedVms += vms.length;	
	}
}