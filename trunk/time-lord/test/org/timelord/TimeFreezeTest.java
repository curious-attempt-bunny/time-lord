package org.timelord;

import java.util.Calendar;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;

public class TimeFreezeTest extends TestCase {
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		Clock.freeze();
	}
	
	public void testFreezesCalendar() throws Exception {
		Calendar expected = Clock.getCalendar();
		
		Thread.sleep(10);
		
		Calendar actual = Clock.getCalendar();
		
		assertEquals(expected, actual);
	}
	
	public void testFreezesCalendar_ForTheCurrentThreadOnly() throws Exception {
		Calendar frozenTime = Clock.getCalendar();
		
		Thread.sleep(10);
		
		final Calendar[] actualWorkerThreadTime = new Calendar[1];
		
		Thread thread = new Thread(new Runnable() {
		
			@Override
			public void run() {
				actualWorkerThreadTime[0] = Clock.getCalendar();
			}
		
		});
		thread.start();
		thread.join();
		
		assertTrue("Freeze should effect only the current thread",
				frozenTime.before(actualWorkerThreadTime[0]));
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		
		Clock.thaw();
	}
	
}
