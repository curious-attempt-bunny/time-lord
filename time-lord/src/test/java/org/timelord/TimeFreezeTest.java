/*
 *  Copyright 2008 Merlyn Albery-Speyer 
 *  
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  
 */

package org.timelord;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import junit.framework.TestCase;

public class TimeFreezeTest extends TestCase {
	@Override
	protected void setUp() throws Exception {
		super.setUp();

		Clock.freeze();
	}

	@Override
	protected void tearDown() throws Exception {
		Clock.thaw();

		super.tearDown();
	}

	public void testFreezesCalendar() throws Exception {
		FreezeTestHelper.testCalendarFrozen();
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

	public void testFreezesCalendar_LocaleConstructor() throws Exception {
		Calendar expected = Calendar.getInstance(Locale.FRANCE);

		Thread.sleep(10);

		Calendar actual = Calendar.getInstance(Locale.FRANCE);

		assertEquals(expected, actual);
	}

	public void testFreezesCalendar_TimezoneConstructor() throws Exception {
		Calendar expected = Calendar.getInstance(TimeZone.getTimeZone("CET"));

		Thread.sleep(10);

		Calendar actual = Calendar.getInstance(TimeZone.getTimeZone("CET"));

		assertEquals(expected, actual);
	}

	public void testFreezesCalendar_TimezoneLocaleConstructor()
			throws Exception {
		Calendar expected = Calendar.getInstance(TimeZone.getTimeZone("PST"),
				Locale.CANADA);

		Thread.sleep(10);

		Calendar actual = Calendar.getInstance(TimeZone.getTimeZone("PST"),
				Locale.CANADA);

		assertEquals(expected, actual);
	}

	public void testFreezesCalendar_ForTheCurrentThreadOnly() throws Exception {
		Calendar frozenTime = Calendar.getInstance();

		Thread.sleep(10);

		final Calendar[] actualWorkerThreadTime = new Calendar[1];

		Thread thread = new Thread(new Runnable() {

			public void run() {
				actualWorkerThreadTime[0] = Calendar.getInstance();
			}

		});
		thread.start();
		thread.join();

		assertTrue("Freeze should effect only the current thread", frozenTime
				.before(actualWorkerThreadTime[0]));
	}

}
