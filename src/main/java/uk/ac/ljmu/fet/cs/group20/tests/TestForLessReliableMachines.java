package uk.ac.ljmu.fet.cs.group20.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;

import org.junit.Test;

import hu.mta.sztaki.lpds.cloud.simulator.iaas.IaaSService;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.PhysicalMachine;
import hu.unimiskolc.iit.distsys.ExercisesBase;
import hu.unimiskolc.iit.distsys.forwarders.PMForwarder;
import hu.unimiskolc.iit.distsys.interfaces.CloudProvider;
import uk.ac.ljmu.fet.cs.group20.CustomCloudProvider;

public class TestForLessReliableMachines {
	@Test
	public void test() throws Exception {
		IaaSService iaas = ExercisesBase.getComplexInfrastructure(30);
		int originalSize = iaas.machines.size();
		ArrayList<Double> originalReliabilities = new ArrayList<>();
		for (PhysicalMachine pm : iaas.machines) {
			originalReliabilities.add(((PMForwarder) pm).getReliMult());
		}
		CloudProvider prov = new CustomCloudProvider();
		prov.setIaaSService(iaas);
		// We drop a machine here to simulate failures
		iaas.deregisterHost(iaas.machines.get(0));
		// We check if the cloudprovider implementation readds the lost machine
		assertEquals("The size of the physical machine list should not change", originalSize, iaas.machines.size());
		ArrayList<Double> newReliabilities = new ArrayList<>();
		for (PhysicalMachine pm : iaas.machines) {
			newReliabilities.add(((PMForwarder) pm).getReliMult());
		}
		Collections.sort(originalReliabilities);
		Collections.sort(newReliabilities);
		boolean alwaysSmaller = true;
		for (int i = 0; i < originalSize; i++) {
			alwaysSmaller &= newReliabilities.get(i) > originalReliabilities.get(i);
		}
		assertTrue("All machines should be replaced with less reliable ones to save capex", alwaysSmaller);
	}
}
