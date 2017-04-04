package uk.ac.ljmu.fet.cs.group20;

import java.util.List;

import org.apache.commons.lang3.RandomUtils;

import hu.mta.sztaki.lpds.cloud.simulator.iaas.IaaSService;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.PhysicalMachine;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.VMManager.CapacityChangeEvent;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.constraints.ResourceConstraints;
import hu.unimiskolc.iit.distsys.CostAnalyserandPricer;
import hu.unimiskolc.iit.distsys.ExercisesBase;
import hu.unimiskolc.iit.distsys.forwarders.IaaSForwarder;
import hu.unimiskolc.iit.distsys.interfaces.CloudProvider;

public class CustomCloudProvider implements CloudProvider, CapacityChangeEvent<PhysicalMachine>{
	
	private IaaSService customProvider;
	private CostAnalyserandPricer costAnalyser;
	private ResourceConstraints rc;
	
	private final double basePrice = 0.0005;
	
	private int vmCount = 0;
	private int pmCount = 0;
	private int singleCoreVmNum = 0;
	private int dualCoreVmNum = 0;
	private int quadCoreVmNum = 0;
	
	private double adjustedBasePrice = 0;
	private double totalPrice;
	private double profit=0;
	
	@Override
	public double getPerTickQuote(ResourceConstraints rc) {
		this.rc = rc;
		calculateNumOfVMs();
		
//		double coreNum = rc.getRequiredCPUs(); //Number of CPU cores
//		double coreClock = rc.getRequiredProcessingPower(); //CPU frequency
		double singleCoreVMPrice = basePrice;
		double dualCoreVMPrice = basePrice * 1.5;
		double quadCoreVMPrice = basePrice * 2;
		double totalPrice; //total price is what we are charging the customer.
		
		for(@SuppressWarnings("unused") PhysicalMachine pM : customProvider.machines){
			if(rc.getRequiredCPUs() == 1){
				this.singleCoreVmNum++;
			}
			if(rc.getRequiredCPUs() == 2 ){
				this.dualCoreVmNum++;
			}
			else{
				this.quadCoreVmNum++;
			}
		}
		
		totalPrice = (singleCoreVmNum * singleCoreVMPrice)+(dualCoreVmNum * dualCoreVMPrice)+(quadCoreVmNum * quadCoreVMPrice);
		
		return (totalPrice * getDiscountAvailable(vmCount));
		
	}
	
	@Override //Method for changing capacity, will automatically replace lost VMs with new ones.
	public void capacityChanged(ResourceConstraints newCapacity, List<PhysicalMachine> affectedCapacity){
	
		final boolean newRegistration = customProvider.isRegisteredHost(affectedCapacity.get(0));
		//If PM is lost, this will execute.
		if(!newRegistration){
			try{
				//For loop which replaces lost PhysicalMachine.
				for (@SuppressWarnings("unused") PhysicalMachine pM : affectedCapacity){
					customProvider.registerHost(ExercisesBase.getNewPhysicalMachine(RandomUtils.nextDouble(2, 5)));
				}
			}catch (Exception e){
				throw new RuntimeException(e);
			}
		}
	}
	
	//Method to set cost analyser.
	public void setCostAnalyser(CostAnalyserandPricer costAnalyser){
		this.costAnalyser = costAnalyser;
	}
	
	//Will automatically adjust base price depending on how good/bad the priovider is doing.
	public void calculateAdjustedBasePrice(CostAnalyserandPricer costAnalyser){
		if(this.profit<0){
			this.adjustedBasePrice = 2 * this.basePrice;
		}
	}
	
	//Method to calculate number of Physical Machine.
	public void calculateNumOfPms(){
		for(@SuppressWarnings("unused") PhysicalMachine pm : customProvider.machines){
			pmCount++;
		}
	}
	
	//Method to calculate number of Virtual Machines.
	public void calculateNumOfVMs(){
		for(PhysicalMachine pM: customProvider.machines){
			this.vmCount += pM.numofCurrentVMs();
		}
	}
	
	public double getDiscountAvailable(int vmCount){
		//If 70+ VMs has been sold, give 25% discount.
		if(vmCount >= 70){
			return 0.75;
		}
		//If 60 - 69 VMs has been sold, give 20% discount.
		if(vmCount >= 60 && vmCount < 70){
			return 0.8;
		}
		//If 50 - 59 VMs has been sold, give 15% discount.
		if(vmCount >= 50 && vmCount < 60){
			return 0.85;
		}
		//If 40 - 49 VMs has been sold, give 10% discount.
		if(vmCount >= 40 && vmCount < 50){
			return 0.9;
		}
		//No discount.
		return 1;
	}
	
	@Override
	public void setIaaSService(IaaSService iaas){
		this.customProvider = iaas;
		iaas.subscribeToCapacityChanges(this);
		((IaaSForwarder) customProvider).setQuoteProvider(this);
	}
}
