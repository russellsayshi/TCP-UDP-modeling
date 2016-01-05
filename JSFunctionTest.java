import javax.script.*;
import java.util.function.*;

public class JSFunctionTest {
    private String script = "";
    private ScriptEngineManager manager = new ScriptEngineManager();
    private ScriptEngine engine = manager.getEngineByName("nashorn");
    protected Invocable inv;
    
    public void setScript(String script) {
        this.script = script;
        
        evalScript();
    }
    
    public void receiveFunction(BiConsumer<Integer, String> func) {
        System.out.println("Function received");
        func.accept(5, "testing");
    }
    
    private void evalScript() {
        try {
            engine.put("jsft", this);
            engine.eval(script);
            inv = (Invocable)engine;
        } catch(ScriptException se) {
            se.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        JSFunctionTest jsft = new JSFunctionTest();
        jsft.setScript("jsft.receiveFunction(function(int, str) {print('Ayy: ' + int + ' ' + str)});");
    }
}