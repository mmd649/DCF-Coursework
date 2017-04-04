package uk.ac.ljmu.fet.cs.group20.testsolution;

import hu.mta.sztaki.lpds.cloud.simulator.iaas.IaaSService;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.IaaSService.IaaSHandlingException;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.PhysicalMachine;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.constraints.ResourceConstraints;
import hu.unimiskolc.iit.distsys.interfaces.CloudProvider;


public class MyFirstProvider implements CloudProvider {

	@Override
	public double getPerTickQuote(ResourceConstraints rc) {
		
		return 0.000002;
	}

	@Override
	public void setIaaSService(IaaSService iaas){
		
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

