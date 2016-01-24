public class Container<T> {
    private T item;
    public Container(T item) {
        this.item = item;
    }
    public Container() {
        item = null;
    }
    public void set(T item) {
        this.item = item;
    }
    public T get() {
        return item;
    }
}