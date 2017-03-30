package uk.ac.ljmu.fet.cs.group20;

import hu.mta.sztaki.lpds.cloud.simulator.iaas.IaaSService;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.constraints.ResourceConstraints;
import hu.unimiskolc.iit.distsys.interfaces.CloudProvider;

public class CustomCloudProvider implements CloudProvider{
	
	@Override
	public double getPerTickQuote(ResourceConstraints rc) {
		
		return 0.000002;
	}
	

	@Override
	public void setIaaSService(IaaSService iaas){
		
	}


}
