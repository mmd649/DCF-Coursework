package uk.ac.ljmu.fet.cs.group20;

import java.util.List;

import org.apache.commons.lang3.RandomUtils;

import hu.mta.sztaki.lpds.cloud.simulator.DeferredEvent;
import hu.mta.sztaki.lpds.cloud.simulator.energy.MonitorConsumption;
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
	private MonitorConsumption[] Monitors;
	
	private final double basePrice = 0.0005;
	
	private int vmCount = 0;
	@SuppressWarnings("unused")
	private int pmCount = 0;
	private int singleCoreVmNum = 0;
	private int dualCoreVmNum = 0;
	private int quadCoreVmNum = 0;
	private double currentTotalConsumption = 0;
	
	private double adjustedBasePrice = 0; //[Not in use at the moment]
	
	@Override
	public double getPerTickQuote(ResourceConstraints rc) {
		this.rc = rc;
		calculateNumOfVMs();
		
		double coreNum = rc.getRequiredCPUs(); //Number of CPU cores
		double coreClock = rc.getRequiredProcessingPower(); //CPU frequency
		double singleCoreVMPrice = basePrice;
		double dualCoreVMPrice = basePrice * 1.5;
		double quadCoreVMPrice = basePrice * 2;
		double totalPrice = (((singleCoreVmNum * singleCoreVMPrice)+(dualCoreVmNum * dualCoreVMPrice)+(quadCoreVmNum * quadCoreVMPrice)))/(coreClock*coreNum);
		
		if (Monitors == null || !Monitors[0].isSubscribed()) {
			return ((basePrice * totalPrice * getDiscountAvailable(vmCount)));
		}else{
			for (MonitorConsumption mon : Monitors) {
				currentTotalConsumption += mon.getSubHourProcessing();
			}
			this.adjustedBasePrice = (currentTotalConsumption / rc.getTotalProcessingPower())*basePrice;
			return ((adjustedBasePrice * totalPrice * getDiscountAvailable(vmCount)));
		}
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
	
	public void LowerPrice4LowerLoad(){
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
	
	//Method to set cost analyser.
	public void setCostAnalyser(CostAnalyserandPricer costAnalyser){
		this.costAnalyser = costAnalyser;
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
		//If 30 - 39 VMs has been sold, give 5% discount.
		if(vmCount >= 30 && vmCount < 40){
			return 0.95;
		}
		//No discount.
		return 1;
	}
	
	@Override
	public void setIaaSService(IaaSService iaas){
		this.customProvider = iaas;
		iaas.subscribeToCapacityChanges(this);
		((IaaSForwarder) customProvider).setQuoteProvider(this);
		LowerPrice4LowerLoad();
	}
}
