package org.timelord;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import junit.framework.TestCase;

public class TimeFreezeTest extends TestCase {
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
	
	/* TODO
	 * Calendar.getInstance(Locale aLocale)
	 * Calendar.getInstance(TimeZone zone)
	 * Calendar.getInstance(TimeZone zone, Locale aLocale) 
	 */
		
	public void testFreezesCalendar() throws Exception {
		Calendar expected = Calendar.getInstance();
		
		Thread.sleep(10);
		
		Calendar actual = Calendar.getInstance();
		
		assertEquals(expected, actual);
	}
	
	public void testFreezesDate() throws Exception {
		Date expected = new Date();
		
		Thread.sleep(10);
		
		Date actual = new Date();
		
		assertEquals(expected, actual);
	}
	
	public void testFreezesCurrentTimeMillis() throws Exception {
		long expected = System.currentTimeMillis();
		
		Thread.sleep(10);
		
		long actual = System.currentTimeMillis();
		
		assertEquals(expected, actual);
	}
	
//	public void testFreezesCalendar_LocaleConstructor() throws Exception {
//		Calendar expected = Calendar.getInstance(Locale.FRANCE);
//		
//		Thread.sleep(10);
//		
//		Calendar actual = Calendar.getInstance(Locale.FRANCE);
//		
//		assertEquals(expected, actual);
//	}
	
	public void testFreezesCalendar_ForTheCurrentThreadOnly() throws Exception {
		Calendar frozenTime = Calendar.getInstance();
		
		Thread.sleep(10);
		
		final Calendar[] actualWorkerThreadTime = new Calendar[1];
		
		Thread thread = new Thread(new Runnable() {
		
			@Override
			public void run() {
				actualWorkerThreadTime[0] = Calendar.getInstance();
			}
		
		});
		thread.start();
		thread.join();
		
		assertTrue("Freeze should effect only the current thread",
				frozenTime.before(actualWorkerThreadTime[0]));
	}
	
}
