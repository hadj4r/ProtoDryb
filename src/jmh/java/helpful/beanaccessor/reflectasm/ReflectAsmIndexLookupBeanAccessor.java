package helpful.beanaccessor.reflectasm;

import com.esotericsoftware.reflectasm.MethodAccess;
import com.hadj4r.samples.models.Point;
import helpful.beanaccessor.BeanAccessor;

public class ReflectAsmIndexLookupBeanAccessor implements BeanAccessor {
    private final MethodAccess access;
    private final int getterMethodIndex;
    private final int setterMethodIndex;

    public ReflectAsmIndexLookupBeanAccessor(final Class<Point> clazz, final String propertyName) {
        this.access = MethodAccess.get(clazz);
        final String getterName = "get" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
        this.getterMethodIndex = access.getIndex(getterName);
        final String setterName = "set" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
        this.setterMethodIndex = access.getIndex(setterName, int.class); // hardcoded for easier testing
    }

    @Override
    public Object executeGetter(final Object bean) {
        return access.invoke(bean, getterMethodIndex);
    }

    @Override
    public void executeSetter(final Object bean, final Object value) {
        access.invoke(bean, setterMethodIndex, value);
    }
}
