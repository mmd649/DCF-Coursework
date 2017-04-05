package uk.ac.ljmu.fet.cs.group20.testsolution;

import java.util.ArrayList;
import java.util.Collections;

import hu.mta.sztaki.lpds.cloud.simulator.iaas.IaaSService;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.IaaSService.IaaSHandlingException;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.PhysicalMachine;
import hu.unimiskolc.iit.distsys.ExercisesBase;
import hu.unimiskolc.iit.distsys.PMPriceRecord;
import hu.unimiskolc.iit.distsys.forwarders.PMForwarder;

public class PMCreator {

	public static PMForwarder createLowerEnergyMachine(PhysicalMachine energyReference) throws SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException {
		double referencePowerDraw=((PMForwarder)energyReference).getMaxConsumption();
		PMForwarder pmNew;
		do {
			pmNew=(PMForwarder)ExercisesBase.getNewPhysicalMachine();
		} while(pmNew.getMaxConsumption()>=referencePowerDraw);
		return pmNew;
	}

	public static void reduceEnergy(IaaSService forService, double withHowManyWatts) throws SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException, IaaSHandlingException {
		double reduction=0;
		do {
			ArrayList<PhysicalMachine> machineSetCopy=new ArrayList<PhysicalMachine>(forService.machines);
			Collections.shuffle(machineSetCopy);
			PhysicalMachine pmOriginal=machineSetCopy.get(0);
			PMForwarder pmReduced=createLowerEnergyMachine(pmOriginal);
			reduction+=((PMForwarder)pmOriginal).getMaxConsumption()-pmReduced.getMaxConsumption();
			forService.deregisterHost(pmOriginal);
			forService.registerHost(pmReduced);
		} while (reduction<withHowManyWatts);
	}

	public static PMForwarder createLowerPricedMachine(PMForwarder priceReference) throws SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException {
		PMPriceRecord recordForReference=new PMPriceRecord(priceReference);
		PMPriceRecord recordForNew;
		do {
			recordForNew=new PMPriceRecord((PMForwarder)ExercisesBase.getNewPhysicalMachine());
		} while(recordForNew.getCurrentMachinePrice()>=recordForReference.getCurrentMachinePrice());
		return recordForNew.pm;
	}

}
