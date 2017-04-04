package uk.ac.ljmu.fet.cs.group20.testsolution;

import hu.mta.sztaki.lpds.cloud.simulator.iaas.IaaSService;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.PhysicalMachine;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.IaaSService.IaaSHandlingException;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.constraints.ResourceConstraints;
import hu.unimiskolc.iit.distsys.interfaces.CloudProvider;

public class LoadDependentProvider implements CloudProvider {
	
	IaaSService iaas;

	@Override
	public double getPerTickQuote(ResourceConstraints rc) {
		double basePrice = 0.000005;
		int pmCount = iaas.machines.size();
		int numOfVm = 0;
		double totalPrice = 0;
		
		for(PhysicalMachine pm : iaas.machines){
			numOfVm += pm.numofCurrentVMs();
			totalPrice += rc.getRequiredCPUs()*basePrice*numOfVm;
		}
		
		return totalPrice;
	}

	@Override
	public void setIaaSService(IaaSService iaas) {
		this.iaas = iaas;
		PhysicalMachine[] pmArray = iaas.machines.toArray(new PhysicalMachine[0]);
		for(int x = 0; x < 15; x++){
			try {
				iaas.deregisterHost(pmArray[x]);
			} catch (IaaSHandlingException e) {
				System.out.println("Error encountered.");
				e.printStackTrace();
				System.exit(5);
				
			}
		}
	}

}
