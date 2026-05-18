@FunctionalInterface
public interface ThrowableConsumer<T> {
    public void execute(T t) throws Throwable;
}
