package helpful.beanaccessor.lambdametafactory;

import helpful.beanaccessor.BeanAccessor;
import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaConversionException;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.function.BiConsumer;
import java.util.function.Function;
import static helpful.utils.PrimitiveConverter.getWrapperClass;

public class LambdaMetafactoryBeanAccessor implements BeanAccessor {
    private final Function getterFunction;
    private final BiConsumer setterFunction;

    public LambdaMetafactoryBeanAccessor(Class<?> beanClass, String propertyName) {
        // Not 100% according to Java Beans spec, contains a bug for getHTTP() IIRC
        String getterName = "get" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
        String setterName = "set" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
        Method getterMethod;
        Method setterMethod;
        try {
            getterMethod = beanClass.getMethod(getterName);
            setterMethod = beanClass.getMethod(setterName, getterMethod.getReturnType());
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("The class (" + beanClass + ") has doesn't have the getter method ("
                    + getterName + ").", e);
        }
        Class<?> returnType = getterMethod.getReturnType();

        MethodHandles.Lookup lookup = MethodHandles.lookup();
        CallSite getterSite;
        CallSite setterSite;
        try {
            getterSite = LambdaMetafactory.metafactory(lookup,
                    "apply",
                    MethodType.methodType(Function.class),
                    MethodType.methodType(Object.class, Object.class),
                    lookup.findVirtual(beanClass, getterName, MethodType.methodType(returnType)),
                    MethodType.methodType(returnType, beanClass));
        } catch (LambdaConversionException | NoSuchMethodException | IllegalAccessException e) {
            throw new IllegalArgumentException("Lambda creation failed for method (" + getterMethod + ").", e);
        }
        try {
            setterSite = LambdaMetafactory.metafactory(lookup,
                    "accept",
                    MethodType.methodType(BiConsumer.class),
                    MethodType.methodType(void.class, Object.class, Object.class),
                    lookup.findVirtual(beanClass, setterName, MethodType.methodType(void.class, returnType)),
                    MethodType.methodType(void.class, beanClass, getWrapperClass(returnType)));
        } catch (LambdaConversionException | NoSuchMethodException | IllegalAccessException e) {
            throw new IllegalArgumentException("Lambda creation failed for method (" + setterName + ").", e);
        }
        try {
            getterFunction = (Function) getterSite.getTarget().invokeExact();
        } catch (Throwable e) {
            throw new IllegalArgumentException("Lambda creation failed for method (" + getterMethod + ").", e);
        }
        try {
            setterFunction = (BiConsumer) setterSite.getTarget().invokeExact();
        } catch (Throwable e) {
            throw new IllegalArgumentException("Lambda creation failed for method (" + setterMethod + ").", e);
        }
    }

    public Object executeGetter(Object bean) {
        return getterFunction.apply(bean);
    }

    @Override
    public void executeSetter(final Object bean, final Object value) {
        setterFunction.accept(bean, value);
    }
}
