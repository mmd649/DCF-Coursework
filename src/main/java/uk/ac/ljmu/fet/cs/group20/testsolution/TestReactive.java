package uk.ac.ljmu.fet.cs.group20.testsolution;

import java.util.List;

import org.apache.commons.lang3.RandomUtils;

import hu.mta.sztaki.lpds.cloud.simulator.DeferredEvent;
import hu.mta.sztaki.lpds.cloud.simulator.Timed;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.IaaSService;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.IaaSService.IaaSHandlingException;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.PhysicalMachine;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.VMManager;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.constraints.ResourceConstraints;
import hu.unimiskolc.iit.distsys.ExercisesBase;
import hu.unimiskolc.iit.distsys.forwarders.IaaSForwarder;
import hu.unimiskolc.iit.distsys.forwarders.IaaSForwarder.QuoteProvider;

public class TestReactive implements VMManager.CapacityChangeEvent<PhysicalMachine>, QuoteProvider{
	
	IaaSService MyReactiveProvider;
	
	
	public void testForDropOuts() throws Exception {
		
		MyReactiveProvider = ExercisesBase.getComplexInfrastructure(30);
		MyReactiveProvider.subscribeToCapacityChanges(this);
		((IaaSForwarder) MyReactiveProvider).setQuoteProvider(this);
		
		new DeferredEvent(2 * 60 * 60 * 1000){ //2 hours deffered event

			@Override
			protected void eventAction() {
				
				// TODO Auto-generated method stub
				int whichMachine = RandomUtils.nextInt(0, MyReactiveProvider.machines.size());
				try {
					
					MyReactiveProvider.deregisterHost(MyReactiveProvider.machines.get(whichMachine));
					Timed.simulateUntilLastEvent();
					assertEquals("The machine has decreased!", 30, MyReactiveProvider.machines.size());
					
				} catch (IaaSHandlingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}			
		};
		
	}

	protected void assertEquals(String string, int i, int size) {
		// TODO Auto-generated method stub
	}

	@Override
	public void capacityChanged(ResourceConstraints newCapacity, List<PhysicalMachine> affectedCapacity) {
		final boolean newRegistration = MyReactiveProvider.isRegisteredHost(affectedCapacity.get(0));
		if(!newRegistration){
			
			try{
				
				for (@SuppressWarnings("unused") PhysicalMachine pM : affectedCapacity){
					
					MyReactiveProvider.registerHost(ExercisesBase.getNewPhysicalMachine(RandomUtils.nextDouble(2, 5)));
				}
				
			}catch (Exception e){
				
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	public double getPerTickQuote(ResourceConstraints rc) {
		// TODO Auto-generated method stub
		return 0.0002;
	}
	
}
