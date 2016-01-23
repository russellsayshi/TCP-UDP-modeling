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
    
    public void ipPrompt() {
        String ip = "";
        do {
            ip = Utility.getInput("Enter ip", "Enter the new IP address for the computer " + node.getIP() + ":");
            if(ip == null) {
                return;
            }
        } while(!Utility.verifyIP(ip));
        node.getNet().changeIP(
            node.getIP(),
            ip);
    }
    
    public static Computer computerFactory(DrawableObject drawable, DisplayPanel dp, BiConsumer<String, String> printCallback, Consumer<ScriptExceptionContainer> errorCallback) {
        String ip = Utility.getNextIP();
        Computer comp = new Computer(null, drawable, dp);
        Node node = new Node(ip, dp.getNetwork(), comp);
        node.initializeWriterWithPrintCallback(printCallback);
        node.setErrorCallback(error -> {
            ScriptExceptionContainer sec = new ScriptExceptionContainer();
            sec.ip = node.getIP();
            sec.exception = error;
            errorCallback.accept(sec); //Propagate callback
        });
        if(!dp.getNetwork().placeNodeAtIP(ip, node)) {
            Utility.displayErrorMonospace("Error", "Cannot create computer at that IP");
            return null;
        }
        comp.setNode(node);
        return comp;
    }   
}