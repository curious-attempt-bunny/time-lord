package org.timelord;

import javassist.ClassPool;
import javassist.CodeConverter;
import javassist.CtClass;

public class TimeLordClassLoader extends java.lang.ClassLoader {
	private ClassPool pool;
	private CodeConverter codeConverter;

	public TimeLordClassLoader(ClassLoader classLoader) {
		super(classLoader);
		
		pool = ClassPool.getDefault();
	}

	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException {
		 System.out.println(name);
		if (!name.startsWith("java.") && !name.startsWith("sun.")) {
			CtClass cc;
			try {
				cc = pool.get(name);

				if (!cc.isInterface()) {
					if (!name.equals("org.timelord.Clock")) {
						System.out.println("Instrumenting "+name);
						
						codeConverter = new CodeConverter();
						CtClass system;
						try {
							system = pool.get("java.lang.System");
							CtClass clock = pool.get("org.timelord.Clock");

							codeConverter
									.redirectMethodCall(
											system
													.getDeclaredMethod("currentTimeMillis"),
											clock
													.getDeclaredMethod("currentTimeMillis"));

						} catch (Exception e) {
							e.printStackTrace();
							throw new RuntimeException(e);
						}

						cc.instrument(codeConverter);
					}
				}

				byte[] b = cc.toBytecode();
				return defineClass(name, b, 0, b.length);
			} catch (Exception e) {
				throw new ClassNotFoundException("", e);
			}
			// System.currentTimeMillis()

		} else {
			Class<?> loadClass = super.loadClass(name);

			return loadClass;
		}
	}
}
