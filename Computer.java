import javax.script.ScriptException;
import java.util.function.*;

public class Computer {
    private Node node;
    private DrawableObject drawable;
    private DisplayPanel dp;
    
    protected Computer(Node node, DrawableObject drawable, DisplayPanel dp) {
        this.node = node;
        this.drawable = drawable;
        this.dp = dp;
    }
    
    protected void setNode(Node node) {
        this.node = node;
    }
    
    public Node getNode() {
        return node;
    }
    
    public DrawableObject getDrawableObject() {
        return drawable;
    }
    
    public static Computer computerFactory(DrawableObject drawable, DisplayPanel dp, BiConsumer<String, String> printCallback, Consumer<ScriptExceptionContainer> errorCallback) {
        String ip = "";
        do {
            ip = Utility.getInput("Enter ip", "Enter the IP address for the computer you are creating (e.g. 10.10.39.45):");
            if(ip == null) {
                return null;
            }
        } while(!Utility.verifyIP(ip));
        Computer comp = new Computer(null, drawable, dp);
        Node node = new Node(ip, dp.getNetwork(), comp);
        node.initializeWriterWithPrintCallback(printCallback);
        node.setErrorCallback(error -> {
            ScriptExceptionContainer sec = new ScriptExceptionContainer();
            sec.ip = node.getIP();
            sec.exception = error;
            errorCallback.accept(sec);
        });
        if(!dp.getNetwork().placeNodeAtIP(ip, node)) {
            Utility.displayErrorMonospace("Error", "Cannot create computer at that IP");
            return null;
        }
        comp.setNode(node);
        return comp;
    }   
}
