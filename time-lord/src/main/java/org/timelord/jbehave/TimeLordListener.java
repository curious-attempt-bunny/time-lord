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
package org.timelord.jbehave;

import java.io.Writer;

import org.jbehave.core.behaviour.Behaviour;
import org.jbehave.core.behaviour.BehaviourClass;
import org.jbehave.core.listener.PlainTextListener;
import org.jbehave.core.util.Timer;
import org.timelord.Clock;
import org.timelord.annotations.TimeLord;

public class TimeLordListener extends PlainTextListener {

	public TimeLordListener(Writer writer, Timer timer) {
		super(writer, timer);
	}

	@Override
	public void after(Behaviour behaviour) {
		if (isTimeLordEnabled(behaviour)) {
			Clock.thaw();
		}
		super.after(behaviour);
	}

	@Override
	public void before(Behaviour behaviour) {
		super.before(behaviour);

		if (isTimeLordEnabled(behaviour)) {
			Clock.freeze();
		}
	}

	private boolean isTimeLordEnabled(Behaviour behaviour) {
		if (behaviour instanceof BehaviourClass) {
			BehaviourClass behaviourClass = (BehaviourClass) behaviour;
			Class<?> clazz = behaviourClass.classToVerify();
			if (clazz.getAnnotation(TimeLord.class) != null) {
				return true;
			}
		}
		return false;
	}

	public static PlainTextListener getInstance(Writer writer, Timer timer) {
		return new TimeLordListener(writer, timer);
	}
}
