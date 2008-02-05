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

public class Clock {

	private static ThreadLocal<ClockThreadState> threadLocalState = new ThreadLocal<ClockThreadState>();

	public static void thaw() {
		getClockState().setFrozen(false);
		getClockState().setTimeNow(0L);
	}

	public static void freeze() {
		getClockState().setFrozen(true);
		getClockState().setTimeNow(System.currentTimeMillis());
	}

	private static ClockThreadState getClockState() {
		ClockThreadState threadState = threadLocalState.get();

		if (threadState == null) {
			threadState = new ClockThreadState();
			threadLocalState.set(threadState);
		}

		return threadState;
	}

	public static Calendar getCalendar() {
		return getCorrectCalendar(Calendar.getInstance());
	}

	public static Calendar getCalendar(Locale locale) {
		return getCorrectCalendar(Calendar.getInstance(locale));
	}

	public static Calendar getCalendar(TimeZone timezone) {
		return getCorrectCalendar(Calendar.getInstance(timezone));
	}

	public static Calendar getCalendar(TimeZone timezone, Locale locale) {
		return getCorrectCalendar(Calendar.getInstance(timezone, locale));
	}

	private static Calendar getCorrectCalendar(Calendar calendar) {
		if (getClockState().isFrozen()) {
			calendar.setTimeInMillis(getClockState().getTimeNow());
		}

		return calendar;
	}

	public static Date getDate() {
		if (getClockState().isFrozen()) {
			return new Date(getClockState().getTimeNow());
		}

		return new Date();
	}

	public static long currentTimeMillis() {
		if (getClockState().isFrozen()) {
			return getClockState().getTimeNow();
		} else {
			return System.currentTimeMillis();
		}
	}

	private static class ClockThreadState {

		private long timeNow;
		private boolean isFrozen;

		public void setTimeNow(long timeNow) {
			this.timeNow = timeNow;
		}

		public long getTimeNow() {
			return timeNow;
		}

		public void setFrozen(boolean isFrozen) {
			this.isFrozen = isFrozen;
		}

		public boolean isFrozen() {
			return isFrozen;
		}

	}
}
