package org.timelord.junit;

import junit.framework.TestCase;

import org.timelord.Clock;

public class TimeLordTestCase extends TestCase {
	public TimeLordTestCase() {

	}

	public TimeLordTestCase(String name) {
		super(name);
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
