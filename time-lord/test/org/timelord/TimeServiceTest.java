package org.timelord;

import junit.framework.TestCase;

public class TimeServiceTest extends TestCase {

	public void testProvidesCalendar() throws Exception {
		assertNotNull(Clock.getCalendar());
	}
	
//	public void testProvidesDate() throws Exception {
//		
//	}
	
//	public void testProvidesSystemMillis() throws Exception {
//	
//}

}
