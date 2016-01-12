import java.io.*;
import java.util.function.*;

/* Utility event-based string writer for console */
class EventWriter extends Writer {
    private StringBuilder sb = new StringBuilder();
    private BiConsumer<String, String> callback;
    private Node node;
    
    public EventWriter(Node node, BiConsumer<String, String> callback) {
        this.callback = callback;
        this.node = node;
    }
    
    @Override
    public void close() {}
    @Override
    public void flush() {
        callback.accept(node.getIP(), sb.toString());
        sb.setLength(0);
    }
    @Override
    public void write(char[] a, int b, int c) {
        sb.append(new String(a, b, c));
    }
}