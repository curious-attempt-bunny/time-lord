package org.timelord;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CodeConverter;
import javassist.CtClass;

public class TimeLordClassLoader extends java.lang.ClassLoader {
	private ClassPool pool;

	public TimeLordClassLoader(ClassLoader classLoader) {
		super(classLoader);

		pool = ClassPool.getDefault();
	}

	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException {
		if (isInstrumentable(name)) {
			return loadInstrumentedClass(name);
		} else {
			return super.loadClass(name);
		}
	}

	private Class<?> loadInstrumentedClass(String name)
			throws ClassFormatError, ClassNotFoundException {
		try {
			CtClass cc = pool.get(name);

			if (!cc.isInterface()) {
				if (!name.equals("org.timelord.Clock")) {
					instrumentClass(name, cc);
				}
			}

			byte[] b = cc.toBytecode();
			return defineClass(name, b, 0, b.length);
		} catch (Exception e) {
			throw new ClassNotFoundException("", e);
		}
	}

	private void instrumentClass(String name, CtClass cc)
			throws CannotCompileException {
		System.out.println("Instrumenting " + name);

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

			CtClass date = pool.get("java.util.Date");
			codeConverter.replaceNew(date, clock, "getDate");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		cc.instrument(codeConverter);
	}

	private boolean isInstrumentable(String name) {
		return !name.startsWith("java.") && !name.startsWith("sun.");
	}
}
