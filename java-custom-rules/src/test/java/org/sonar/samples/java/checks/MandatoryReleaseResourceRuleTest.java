package org.sonar.samples.java.checks;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.sonar.java.checks.verifier.JavaCheckVerifier;

public class MandatoryReleaseResourceRuleTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void test() {
		JavaCheckVerifier.verify("src/test/files/AirFareEJB.java", new MandatoryReleaseResourceRule());
	}

}
