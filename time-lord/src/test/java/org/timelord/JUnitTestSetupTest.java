package org.timelord;

import java.util.Calendar;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.timelord.junit.TimeLordTestSetup;

public class JUnitTestSetupTest extends TestCase {
	public static Test suite() {
		TestSuite suite = new TestSuite();

		suite.addTestSuite(JUnitTestSetupTest.class);

		return new TimeLordTestSetup(suite);
	}

	public void testFrozen() throws Exception {
		Calendar expected = Calendar.getInstance();

		Thread.sleep(10);

		Calendar actual = Calendar.getInstance();

		assertEquals(expected, actual);
	}
}
