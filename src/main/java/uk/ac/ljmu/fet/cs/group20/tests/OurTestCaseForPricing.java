package uk.ac.ljmu.fet.cs.group20.tests;

import java.lang.reflect.InvocationTargetException;

import org.junit.Assert;
import org.junit.Test;

import hu.mta.sztaki.lpds.cloud.simulator.iaas.IaaSService;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.IaaSService.IaaSHandlingException;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.PhysicalMachine;
import hu.unimiskolc.iit.distsys.ExercisesBase;
import hu.unimiskolc.iit.distsys.PMPriceRecord;
import hu.unimiskolc.iit.distsys.forwarders.PMForwarder;
import uk.ac.ljmu.fet.cs.group20.testsolution.PMCreator;

public class OurTestCaseForPricing {
	@Test
	public void checkIfLowerEnergyMachineIsReturned()
			throws SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException {
		PMForwarder pmReference = (PMForwarder) ExercisesBase.getNewPhysicalMachine();
		PMForwarder pmBetter = PMCreator.createLowerEnergyMachine(pmReference);
		double referenceConsumption = pmReference.getMaxConsumption();
		double betterConsumption = pmBetter.getMaxConsumption();
		Assert.assertTrue("Please improve the consumption of the machine I am asking from you",
				betterConsumption < referenceConsumption);
	}

	public double getTotalConsumptionForIaaS(IaaSService forWhatService) {
		double totalConsumption = 0;
		for (PhysicalMachine pm : forWhatService.machines) {
			totalConsumption += ((PMForwarder) pm).getMaxConsumption();
		}
		return totalConsumption;
	}

	@Test
	public void makeAnIaaSLessEnergyConsuming()
			throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException, NoSuchFieldException, IaaSHandlingException {
		final double theReductionAsked = 1000; // W
		IaaSService service = ExercisesBase.getComplexInfrastructure(100);
		double consumptionBefore = getTotalConsumptionForIaaS(service);
		PMCreator.reduceEnergy(service, theReductionAsked);
		double consumptionAfter = getTotalConsumptionForIaaS(service);
		Assert.assertTrue(
				"The reduction did not occur, the power draw of all the machines in the IaaS needs to be lower by a "
						+ theReductionAsked + "W",
				consumptionAfter + theReductionAsked < consumptionBefore);
		
		System.out.println(consumptionBefore);
		System.out.println(consumptionAfter);
	}
	
	@Test
	public void lowerPricedMachine() throws SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException {
		PMForwarder pmReference = (PMForwarder) ExercisesBase.getNewPhysicalMachine();
		PMForwarder pmBetter = PMCreator.createLowerPricedMachine(pmReference);
		PMPriceRecord refRecord=new PMPriceRecord(pmReference);
		PMPriceRecord betterRecord=new PMPriceRecord(pmBetter);
		double referencePrice = refRecord.getCurrentMachinePrice();
		double betterPrice = betterRecord.getCurrentMachinePrice();
		Assert.assertTrue("Please improve the price of the machine I am asking from you",
				betterPrice < referencePrice);
	}
}
