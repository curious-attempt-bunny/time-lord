package org.timelord;

import junit.framework.TestCase;

public class ClassLoaderTimeFreezeTest extends TestCase {
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		Clock.freeze();
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		
		Clock.thaw();
	}
	
	public void testFreezesCurrentTimeMillis() throws Exception {
		long expected = System.currentTimeMillis();
		
		Thread.sleep(10);
		
		long actual = System.currentTimeMillis();
		
		assertEquals(expected, actual);
	}
}
