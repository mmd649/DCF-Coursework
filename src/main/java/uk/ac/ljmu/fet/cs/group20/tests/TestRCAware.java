package uk.ac.ljmu.fet.cs.group20.tests;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import hu.mta.sztaki.lpds.cloud.simulator.iaas.IaaSService;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.constraints.ConstantConstraints;
import hu.unimiskolc.iit.distsys.ExercisesBase;
import hu.unimiskolc.iit.distsys.interfaces.CloudProvider;
import uk.ac.ljmu.fet.cs.group20.CustomCloudProvider;

public class TestRCAware {
	
	CloudProvider prov;

	@Before
	public void initializeTest() throws Exception {
		final IaaSService myIaaS = ExercisesBase.getComplexInfrastructure(30);
		prov = new CustomCloudProvider();
		prov.setIaaSService(myIaaS);
	}

	@Test
	public void testConstraintsAwareness() {
		ConstantConstraints ccSmaller = new ConstantConstraints(1, 1, 1);
		ConstantConstraints[] ccBigger = new ConstantConstraints[] { new ConstantConstraints(2, 1, 1),
				new ConstantConstraints(1, 2, 1), new ConstantConstraints(1, 1, 2), new ConstantConstraints(2, 2, 2) };
		double baseQuote = prov.getPerTickQuote(ccSmaller);
		for (int i = 0; i < ccBigger.length; i++) {
			Assert.assertTrue("Could not provide a more expensive quote for " + ccBigger[i],
					prov.getPerTickQuote(ccBigger[i]) > baseQuote);
		}
	}

	@Test
	public void testTransitivity() {
		ConstantConstraints ccSmaller = new ConstantConstraints(1, 1, 1);
		ConstantConstraints ccMedium = new ConstantConstraints(2, 2, 2);
		ConstantConstraints ccBig = new ConstantConstraints(2, 3, 2);
		double smallQuote = prov.getPerTickQuote(ccSmaller);
		double mediumQuote = prov.getPerTickQuote(ccMedium);
		double bigQuote = prov.getPerTickQuote(ccBig);
		Assert.assertTrue("Quotes should be transitive", mediumQuote < bigQuote && smallQuote < bigQuote);
	}
}
