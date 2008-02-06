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

import java.util.HashSet;
import java.util.Set;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CodeConverter;
import javassist.CtClass;
import javassist.NotFoundException;

public class TimeLordClassLoader extends ClassLoader {
	private final ClassPool pool;
	private final Set<String> initialPackages;

	public TimeLordClassLoader(ClassLoader classLoader) {
		super(classLoader);

		pool = ClassPool.getDefault();
		initialPackages = new HashSet<String>();

		for (Package p : getPackages()) {
			initialPackages.add(p.getName());
		}
	}

	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException {
		try {
			if (isInstrumentable(name)) {
				return loadAndInstrumentClass(name);
			}
		} catch (Throwable e) {
			// if we fail to instrument we'll use the default system class
			// loader
		}

		return super.loadClass(name);
	}

	private boolean isInstrumentable(String name) {
		int pos = name.lastIndexOf('.');
		if (pos == -1) {
			return true;
		}

		String packageName = name.substring(0, pos);

		if (packageName.equals("org.timelord")) {
			return true;
		}

		boolean isInitialPackage = initialPackages.contains(packageName);

		// if (isInitialPackage) {
		// System.out.println("Initial package is not instrumentable: "
		// + packageName);
		// }

		return !isInitialPackage;
	}

	private Class<?> loadAndInstrumentClass(String name) throws Exception {
		CtClass cc = pool.get(name);

		if (!cc.isInterface()) {
			if (!isTimelordClockImplementation(name)) {
				instrumentClass(name, cc);
			}
		}

		byte[] b = cc.toBytecode();
		return defineClass(name, b, 0, b.length);

	}

	private boolean isTimelordClockImplementation(String name) {
		return name.equals("org.timelord.Clock")
				|| name.equals("org.timelord.jbehave.TimeLordListener");
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

			if (isJBehaveInsertionPoint(name)) {
				patchJBehave(codeConverter);
			}
		} catch (Exception e) {
			throw new RuntimeException("Failed to create CodeCoverter", e);
		}

		cc.instrument(codeConverter);
	}

	private void patchJBehave(CodeConverter codeConverter)
			throws NotFoundException {
		// NOTE this is a dirty hack to get JBehave to support our TimeLord
		// annotation
		CtClass listener = pool
				.get("org.jbehave.core.listener.PlainTextListener");

		CtClass writer = pool.get("java.io.Writer");
		CtClass timer = pool.get("org.jbehave.core.util.Timer");

		CtClass ourListener = pool.get("org.timelord.jbehave.TimeLordListener");
		codeConverter.replaceNew(listener, ourListener, "getInstance");
	}

	private boolean isJBehaveInsertionPoint(String name) {
		return "org.jbehave.core.BehaviourRunner".equals(name);
	}

}
