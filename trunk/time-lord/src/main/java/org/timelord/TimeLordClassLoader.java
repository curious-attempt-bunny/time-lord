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

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CodeConverter;
import javassist.CtClass;

public class TimeLordClassLoader extends java.lang.ClassLoader {
	private final ClassPool pool;

	public TimeLordClassLoader(ClassLoader classLoader) {
		super(classLoader);

		pool = ClassPool.getDefault();
	}

	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException {
		return loadAndInstrumentClass(name);
	}

	private Class<?> loadAndInstrumentClass(String name)
			throws ClassFormatError, ClassNotFoundException {
		try {
			CtClass cc = pool.get(name);

			if (!cc.isInterface()) {
				if (!isTimelordClockImplementation(name)) {
					instrumentClass(name, cc);
				}
			}

			byte[] b = cc.toBytecode();
			return defineClass(name, b, 0, b.length);
		} catch (Exception e) {
			// we will fail to instrument system classes, for instance
			return super.loadClass(name);
		}
	}

	private boolean isTimelordClockImplementation(String name) {
		return name.equals("org.timelord.Clock");
	}

	private void instrumentClass(String name, CtClass cc)
			throws CannotCompileException {
		// TODO cache this CodeCoverter
		CodeConverter codeConverter = new CodeConverter();
		try {
			CtClass clock = pool.get("org.timelord.Clock");

			CtClass system = pool.get("java.lang.System");
			codeConverter.redirectMethodCall(system
					.getDeclaredMethod("currentTimeMillis"), clock
					.getDeclaredMethod("currentTimeMillis"));

			CtClass calendar = pool.get("java.util.Calendar");
			codeConverter.redirectMethodCall(calendar
					.getDeclaredMethod("getInstance"), clock
					.getDeclaredMethod("getCalendar"));

			CtClass locale = pool.get("java.util.Locale");
			codeConverter
					.redirectMethodCall(calendar.getDeclaredMethod(
							"getInstance", new CtClass[] { locale }), clock
							.getDeclaredMethod("getCalendar",
									new CtClass[] { locale }));

			CtClass timezone = pool.get("java.util.TimeZone");
			codeConverter.redirectMethodCall(calendar.getDeclaredMethod(
					"getInstance", new CtClass[] { timezone }), clock
					.getDeclaredMethod("getCalendar",
							new CtClass[] { timezone }));

			codeConverter.redirectMethodCall(calendar.getDeclaredMethod(
					"getInstance", new CtClass[] { timezone, locale }), clock
					.getDeclaredMethod("getCalendar", new CtClass[] { timezone,
							locale }));

			CtClass date = pool.get("java.util.Date");
			codeConverter.replaceNew(date, clock, "getDate");
		} catch (Exception e) {
			throw new RuntimeException("Failed to create CodeCoverter", e);
		}

		cc.instrument(codeConverter);
	}

}
