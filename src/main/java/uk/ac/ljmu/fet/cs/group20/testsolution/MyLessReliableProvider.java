package uk.ac.ljmu.fet.cs.group20.testsolution;

import java.util.ArrayList;
import java.util.Collections;

import hu.mta.sztaki.lpds.cloud.simulator.iaas.IaaSService;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.IaaSService.IaaSHandlingException;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.PhysicalMachine;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.constraints.ResourceConstraints;
import hu.unimiskolc.iit.distsys.ExercisesBase;
import hu.unimiskolc.iit.distsys.forwarders.PMForwarder;
import hu.unimiskolc.iit.distsys.interfaces.CloudProvider;

public class MyLessReliableProvider implements CloudProvider {

	@Override
	public double getPerTickQuote(ResourceConstraints rc) {
		return 0;
	}

	@Override
	public void setIaaSService(IaaSService iaas) {
		
		ArrayList<PhysicalMachine> machineSetCopy = new ArrayList<PhysicalMachine>(iaas.machines);
		Collections.shuffle(machineSetCopy);
		PhysicalMachine pmOriginal=machineSetCopy.get(0);
		PMForwarder pmReduced = null;
		try {
			pmReduced = lessReliableMachine(pmOriginal);
		} catch (SecurityException | InstantiationException | IllegalAccessException | NoSuchFieldException e) {
			e.printStackTrace();
			System.exit(5);
		}
		try {
			iaas.deregisterHost(pmOriginal);
		} catch (IaaSHandlingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		iaas.registerHost(pmReduced);
	}
	
	public static PMForwarder lessReliableMachine(PhysicalMachine a) throws SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException{
		PMForwarder pmNew;
		int x = 0;
		
		do {
			pmNew=(PMForwarder)ExercisesBase.getNewPhysicalMachine();
		} while(x != 30);
		
		return pmNew;
	}
}