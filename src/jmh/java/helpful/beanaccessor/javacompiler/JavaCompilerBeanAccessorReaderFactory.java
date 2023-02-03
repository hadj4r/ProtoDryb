package helpful.beanaccessor.javacompiler;

import helpful.beanaccessor.BeanAccessor;
import java.lang.reflect.InvocationTargetException;

public class JavaCompilerBeanAccessorReaderFactory {

    private JavaCompilerBeanAccessorReaderFactory() {
    }

    public static BeanAccessor generate(Class<?> beanClass, String propertyName) {
        // Not 100% according to Java Beans spec, contains a bug for getHTTP() IIRC
        String getterName = "get" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
        String setterName = "set" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
        String packageName = JavaCompilerBeanAccessorReaderFactory.class.getPackage().getName()
                + ".generated." + beanClass.getPackage().getName();
        String simpleClassName = beanClass.getSimpleName() + "$" + propertyName;
        String fullClassName = packageName + "." + simpleClassName;
        final String source = "package " + packageName + ";\n"
                + "public class " + simpleClassName + " implements " + BeanAccessor.class.getName() + " {\n"
                + "    public Object executeGetter(Object bean) {\n"
                + "        return ((" + beanClass.getName() + ") bean)." + getterName + "();\n"
                + "    }\n"
                + "    public void executeSetter(Object bean, Object value) {\n"
                + "        ((" + beanClass.getName() + ") bean)." + setterName + "((int)value);\n" // cast to hardcoded int for easier testing
                + "    }\n"
                + "}";
        StringGeneratedJavaCompilerFacade compilerFacade = new StringGeneratedJavaCompilerFacade(
                JavaCompilerBeanAccessorReaderFactory.class.getClassLoader());
        Class<? extends BeanAccessor> compiledClass = compilerFacade.compile(
                fullClassName, source, BeanAccessor.class);
        try {
            return compiledClass.getDeclaredConstructor().newInstance();
        } catch (InvocationTargetException | NoSuchMethodException | InstantiationException |
                 IllegalAccessException e) {
            throw new IllegalStateException("The generated class (" + fullClassName + ") failed to instantiate.", e);
        }
    }
}
