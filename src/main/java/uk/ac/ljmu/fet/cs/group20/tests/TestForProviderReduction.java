package uk.ac.ljmu.fet.cs.group20.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import hu.mta.sztaki.lpds.cloud.simulator.iaas.IaaSService;
import hu.unimiskolc.iit.distsys.ExercisesBase;
import hu.unimiskolc.iit.distsys.interfaces.CloudProvider;
import uk.ac.ljmu.fet.cs.group20.CustomCloudProvider;

public class TestForProviderReduction {
	
	IaaSService underlyingDataCentre;
	CloudProvider myVeryFirstProvider;
	
	@Test
	public void testReduction() throws Exception{
		
		underlyingDataCentre = ExercisesBase.getComplexInfrastructure(30); //Creating a data centre with 30 machines
		myVeryFirstProvider = new CustomCloudProvider();                       //Cloud provider
		myVeryFirstProvider.setIaaSService(underlyingDataCentre);
		assertEquals("Different machine counts", 30, underlyingDataCentre.machines.size());     //Assume that 15 of the 30 computers are used
		
	}
	
}
