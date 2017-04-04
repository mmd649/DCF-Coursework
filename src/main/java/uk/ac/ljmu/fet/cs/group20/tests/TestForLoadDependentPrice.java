package uk.ac.ljmu.fet.cs.group20.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Assert;
import org.junit.Test;

import hu.mta.sztaki.lpds.cloud.simulator.Timed;
import hu.mta.sztaki.lpds.cloud.simulator.energy.MonitorConsumption;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.IaaSService;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.PhysicalMachine;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.constraints.ConstantConstraints;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.constraints.ResourceConstraints;
import hu.unimiskolc.iit.distsys.ExercisesBase;
import hu.unimiskolc.iit.distsys.MultiCloudUser;
import hu.unimiskolc.iit.distsys.interfaces.CloudProvider;
import uk.ac.ljmu.fet.cs.group20.CustomCloudProvider;

public class TestForLoadDependentPrice {
	
	@Test(timeout = 10000)
	public void testLDP() throws Exception {
		// Enable callbacks
		final boolean[] callbackArrived = new boolean[1];
		callbackArrived[0] = false;
		// Set up our datacentre
		IaaSService iaas = ExercisesBase.getComplexInfrastructure(30);
		//Set up our pricing solution
		final CloudProvider singleProvider = new CustomCloudProvider();
		singleProvider.setIaaSService(iaas);
		//Start the user
		MultiCloudUser user = new MultiCloudUser(new IaaSService[] { iaas }, new MultiCloudUser.CompletionCallback() {
			@Override
			public void alljobsComplete() {
				callbackArrived[0] = true;
			}
		});
		// note we could use a deferred event as well in the below line,
		// instead we do not simulate further
		Timed.simulateUntil(2 * 24 * 60 * 60 * 1000); // wait for two days
		final MonitorConsumption[] monitors = new MonitorConsumption[iaas.machines.size()];
		int monitorIndex = 0;
		// Initialize the monitors
		for (PhysicalMachine pm : iaas.machines) {
			// This will be the monitor of pm, and a second last for 1000 ticks
			monitors[monitorIndex++] = new MonitorConsumption(pm, 1000);
		}
		class Analyser extends Timed {
			// this is the resource set for which we will get the quote
			final ResourceConstraints rc = new ConstantConstraints(1, 1, 1);
			// used in the termination condition for the monitors
			int analysisCount = 0;
			// remembers the previous details
			double previousTotal = -1;
			double previousPrice = -1;

			// prepares the event system
			public Analyser() {
				subscribe(10 * 60 * 1000); // do this every ten mins
			}

			@Override
			public void tick(long fires) {
				// The termination condition
				if (analysisCount++ == 100) {
					// Notify the monitors about termination
					for (MonitorConsumption monitor : monitors) {
						monitor.cancelMonitoring();
					}
				}
				// Analysing the current state of the system
				double currentTotal = 0;
				for (MonitorConsumption monitor : monitors) {
					// maximum hourly processing is
					// pm.getPerTickProcessingPower()*60*60*1000
					// but we might receive smaller quantities
					currentTotal += monitor.getSubHourProcessing();
				}
				// Getting a current quote (under the above load conditions
				double currentPrice = singleProvider.getPerTickQuote(rc);
				// Here we assume no machines change from the previous run
				Assert.assertTrue("Should have a better price for lower load",
						// checks if we called this the first time
						previousTotal >= 0 ?
							// if not then:
								// If total is bigger, price must be bigger, if total is lower price must be lower
								((Double) previousTotal).compareTo(currentTotal) ==
								((Double) previousPrice).compareTo(currentPrice)
							// If yes then:
							: true);
				previousTotal=currentTotal;
				previousPrice=currentPrice;
			}
		}
		// Launch the analyiser
		new Analyser();
		Timed.simulateUntilLastEvent();
		assertEquals("Different machine counts", 15, iaas.machines.size());
		Assert.assertTrue("The user should finish", callbackArrived[0]);
	}
}
