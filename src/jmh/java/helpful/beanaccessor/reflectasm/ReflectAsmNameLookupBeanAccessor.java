package helpful.beanaccessor.reflectasm;

import com.esotericsoftware.reflectasm.MethodAccess;
import com.hadj4r.samples.models.Point;
import helpful.beanaccessor.BeanAccessor;

public class ReflectAsmNameLookupBeanAccessor implements BeanAccessor {
    private final MethodAccess access;
    private final String getterName;
    private final String setterName;

    public ReflectAsmNameLookupBeanAccessor(final Class<Point> clazz, final String propertyName) {
        this.access = MethodAccess.get(clazz);
        this.getterName = "get" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
        this.setterName = "set" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
    }

    @Override
    public Object executeGetter(final Object bean) {
        return access.invoke(bean, getterName);
    }

    @Override
    public void executeSetter(final Object bean, final Object value) {
        access.invoke(bean, setterName, value);
    }
}
