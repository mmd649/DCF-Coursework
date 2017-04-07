package uk.ac.ljmu.fet.cs.group20.testsolution;

import hu.mta.sztaki.lpds.cloud.simulator.iaas.IaaSService;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.constraints.ResourceConstraints;
import hu.unimiskolc.iit.distsys.interfaces.CloudProvider;

public class MyRCAwareProvider implements CloudProvider {
	
	IaaSService iaas;
	
	/**
	 * I used rc.getRequiredCPU to check how many cores the vm has. If the vm has 2 cores, it has twice the price of a single core vm.
	 * The calculation for total price is also quite simple. totalPrice = pmCount * basePrice * rc.getRequiredProcessingPower() * rc.getRequiredMemory()
	 */
	@Override
	public double getPerTickQuote(ResourceConstraints rc) {
		double basePrice = 0.000001;
		int pmCount = iaas.machines.size();
		double totalPrice = pmCount * basePrice * rc.getRequiredProcessingPower() * rc.getRequiredMemory();
		
		if(rc.getRequiredCPUs()>1){
			
			totalPrice = 2 * totalPrice;
			
			return totalPrice;
		}else{
	
			return totalPrice;
		}
	}

	@Override
	public void setIaaSService(IaaSService iaas) {
		this.iaas = iaas;
	}

}
