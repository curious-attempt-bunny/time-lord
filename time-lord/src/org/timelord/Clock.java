package org.timelord;

import java.util.Calendar;

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
		Calendar result = Calendar.getInstance();
		
		if (getClockState().isFrozen()) {
			result.setTimeInMillis(getClockState().getTimeNow());
		}
		
		return result;
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
