package uk.ac.ljmu.fet.cs.group20;

import java.util.List;

import org.apache.commons.lang3.RandomUtils;

import hu.mta.sztaki.lpds.cloud.simulator.DeferredEvent;
import hu.mta.sztaki.lpds.cloud.simulator.energy.MonitorConsumption;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.IaaSService;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.PhysicalMachine;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.VMManager.CapacityChangeEvent;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.constraints.ResourceConstraints;
import hu.unimiskolc.iit.distsys.ExercisesBase;
import hu.unimiskolc.iit.distsys.forwarders.IaaSForwarder;
import hu.unimiskolc.iit.distsys.interfaces.CloudProvider;

public class CustomCloudProvider implements CloudProvider, CapacityChangeEvent<PhysicalMachine>{
	
	private IaaSService customProvider;
	private ResourceConstraints rc;
	private MonitorConsumption[] Monitors;
	
	private final double basePrice = 0.0005;
	
	private int vmCount = 0;
	@SuppressWarnings("unused")
	private int pmCount = 0;
	private int singleCoreVmNum = 0;
	private int dualCoreVmNum = 0;
	private int quadCoreVmNum = 0;
	private double currentTotalConsumption = 0;
	
	private double adjustedBasePrice = 0;
	
	
	/**
	 * The pricing strategy of this cloud provider looks at how many cpu cores the vm has. The calculations of vm cores can be found in the calculateNumOfVMs() method at line 109
	 * Once the vms with different core numbers had been sorted out, the base price for each is then work out. As you can see below, a dual core vms is 50% more expensive than a single core.
	 * The total price is then work out with totalPrice = (singleCoreVmNum * singleCoreVMPrice)+(dualCoreVmNum * dualCoreVMPrice)+(quadCoreVmNum * quadCoreVMPrice) / cpu cores * cpu clock
	 * Further discount can be obtained which looks at number of vms sold and gives better discount depending on how many vms has been purchased.
	 * the adjusted baseprice is used when partial cpu load is used and not the whole processing power.
	 */
	@Override
	public double getPerTickQuote(ResourceConstraints rc) {
		this.rc = rc;
		calculateNumOfVMs();
		calculateNumOfPMs();
	
		double singleCoreVMPrice = basePrice;
		double dualCoreVMPrice = basePrice * 1.5;
		double quadCoreVMPrice = basePrice * 2;
		double totalPrice = (((singleCoreVmNum * singleCoreVMPrice)+(dualCoreVmNum * dualCoreVMPrice)+(quadCoreVmNum * quadCoreVMPrice)))/(rc.getTotalProcessingPower());
		
		if (Monitors == null || !Monitors[0].isSubscribed()) {
			return ((basePrice * totalPrice * getDiscountAvailable(vmCount)));
			}
		for (MonitorConsumption mon : Monitors) {
			currentTotalConsumption += mon.getSubHourProcessing();
		}
			this.adjustedBasePrice = (currentTotalConsumption / rc.getTotalProcessingPower())*basePrice;
			return ((adjustedBasePrice * totalPrice * getDiscountAvailable(vmCount)));
	}
	
	/**
	 * This method is for capacity change. It will replace lost physical machines.  
	 */
	@Override
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
	
	public void reducedEnergyChange(List<PhysicalMachine> affectedCapacity)  { // method for checking the machine loads the lowest energy machine.
		
	}
	
	/**
	 * This is the method use for monitoring ongoing processes. It links a monitor to a virtual machine and observes the processing done.
	 */
	public void monitorVMs(){
		new DeferredEvent(2 * 24 * 60 * 60 * 1000 + 1) {
			@Override
			protected void eventAction() {
				Monitors = new MonitorConsumption[customProvider.machines.size()];
				int i = 0;
				for (PhysicalMachine pm : customProvider.machines) {
					Monitors[i++] = new MonitorConsumption(pm, 1000);
				}
				new DeferredEvent(102 * 10 * 60 * 1000l) {
					@Override
					protected void eventAction() {
						for (MonitorConsumption mon : Monitors) {
							mon.cancelMonitoring();
						}
					}
				};
			}
		};
	}
	
	/**
	 * Simple method which determines the number of physical machine.
	 */
	public void calculateNumOfPMs(){
		for(@SuppressWarnings("unused") PhysicalMachine pm : customProvider.machines){
			pmCount++;
		}
	}
	
	/**
	 * This is the method which calculated the number of vms and also sorts them out according to the number of cores they have. This is used when calculating the totalPrice.
	 */
	public void calculateNumOfVMs(){
		for(PhysicalMachine pM: customProvider.machines){
			this.vmCount += pM.numofCurrentVMs();
			
			//Loop to sort vms together. 3 groups which are -  single core VMs, dual core VMs and quadcore VMs 
			if(rc.getRequiredCPUs() == 1){
				this.singleCoreVmNum++;
			}
			else if(rc.getRequiredCPUs() == 2 ){
				this.dualCoreVmNum++;
			}
			else{
				this.quadCoreVmNum++;//Although this is called quadcore, it also include Vms with more than 4 cores.
			}
			
			
		}
	}
	
	
	/**
	 * This method is used to calculate the availanble discount that customer can get depending on how many vms are purchased. 
	 * @param vmCount
	 * @return
	 */
	public double getDiscountAvailable(int vmCount){
		//If 70+ VMs has been sold, give 25% discount.
		if(vmCount >= 25){
			return 0.75;
		}
		//If 60 - 69 VMs has been sold, give 20% discount.
		if(vmCount >= 20 && vmCount < 25){
			return 0.8;
		}
		//If 50 - 59 VMs has been sold, give 15% discount.
		if(vmCount >= 15 && vmCount < 20){
			return 0.85;
		}
		//If 40 - 49 VMs has been sold, give 10% discount.
		if(vmCount >= 10 && vmCount < 15){
			return 0.9;
		}
		//If 5 -9 VMs has been sold, give 5% discount.
		if(vmCount >= 5 && vmCount < 10){
			return 0.95;
		}
		//No discount.
		return 1;
	}
	
	/**
	 * The method which we set the IaaSService as the name suggest.
	 */
	@Override
	public void setIaaSService(IaaSService iaas){
		this.customProvider = iaas;
		iaas.subscribeToCapacityChanges(this);
		((IaaSForwarder) customProvider).setQuoteProvider(this);
		monitorVMs();
	}
}
