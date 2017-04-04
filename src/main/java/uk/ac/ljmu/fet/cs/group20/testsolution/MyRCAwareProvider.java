package uk.ac.ljmu.fet.cs.group20.testsolution;

import hu.mta.sztaki.lpds.cloud.simulator.iaas.IaaSService;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.constraints.ResourceConstraints;
import hu.unimiskolc.iit.distsys.interfaces.CloudProvider;

public class MyRCAwareProvider implements CloudProvider {
	
	IaaSService iaas;

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
