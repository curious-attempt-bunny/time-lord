package org.timelord.junit;

import junit.extensions.TestSetup;
import junit.framework.Test;

import org.timelord.Clock;

public class TimeLordTestSetup extends TestSetup {

	public TimeLordTestSetup(Test test) {
		super(test);
	}

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
}
