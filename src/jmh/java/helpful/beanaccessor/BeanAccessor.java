package helpful.beanaccessor;

public interface BeanAccessor {
    Object executeGetter(Object bean);

    void executeSetter(Object bean, Object value);
}
