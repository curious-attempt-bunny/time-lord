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

import org.junit.Assert;

public class TestHelper {

	public static void testCalendarFrozen() throws Exception {
		Calendar expected = Calendar.getInstance();

		Thread.sleep(10);

		Calendar actual = Calendar.getInstance();

		Assert.assertEquals(expected, actual);
	}

}
