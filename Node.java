import javax.script.*;
import java.util.function.*;
import java.util.*;

class Node {
    private String script = "";
    private ScriptEngineManager manager = new ScriptEngineManager();
    private ScriptEngine engine = manager.getEngineByName("nashorn");
    protected Invocable inv;
    private TCP tcp;
    private UDP udp;
    private NodeUtility nodeutility;
    private Consumer<ScriptException> errorCallback;
    private boolean working = false;
    private String ip;
    private Network net;
    private Computer computer;
    
    public interface ReceiveFunction {
        void accept(String str0, Integer int0, String str1);
    }
    
    public class TCP {
        private ArrayList<TCPConnection> servers = new ArrayList<>();
        private ArrayList<TCPConnection> clients = new ArrayList<>();
        
        public TCPConnection createServer(int port) {
            System.out.println("Creating server at " + port);
            
            //If server already exists at that port
            TCPConnection serv = getServerAtPort(port);
            if(serv != null) {
                return null;
            }
            
            TCPConnection con = new TCPConnection(port);
            servers.add(con);
            return con;
        }
        public TCPConnection connectToServer(String ip, int port) {
            System.out.println("Connecting to server at: " + ip + ":" + port);
            TCPConnection con = new TCPConnection(ip, port);
            clients.add(con);
            return con;
        }
        public TCPConnection getServerAtPort(int port) {
            for(TCPConnection server : servers) {
                if(server.getPort() == port) { 
                    return server;
                }
            }
            return null;
        }
        public TCPConnection getClientAtIPAndPort(String ip, int port) {
            for(TCPConnection client : clients) {
                if(client.getPort() == port && client.getRemoteIP().equals(ip)) { 
                    return client;
                }
            }
            return null;
        }
         
        //Acts as both a TCP client and TCP server
        public class TCPConnection {
            private int port;
            private Consumer<String> onConnection;
            private Consumer<String> onData;
            private Consumer<Boolean> onDisconnect;
            private boolean hasConnection = false;
            private TCPConnectionData otherEnd;
            private boolean isServer;
            private ArrayList<TCPConnectionData> clients;
            
            public class TCPConnectionData {
                public String remoteIP;
                public int port;
                public TCPConnection object;
            }
            
            //Create server
            public TCPConnection(int port) {
                this.port = port;
                isServer = true;
                clients = new ArrayList<TCPConnectionData>();
            }
            
            public ArrayList<TCPConnectionData> getClients() {
                if(isServer) {
                    return clients;
                }
                return null;
            }
            
            //Create client
            public TCPConnection(String remoteIP, int port) {
                this.otherEnd.remoteIP = remoteIP;
                this.port = port;
                isServer = false;
                otherEnd = new TCPConnectionData();
            }
            
            public boolean isServer() {
                return isServer;
            }
            
            public TCPConnection onConnection(Consumer<String> jso) {
                onConnection = jso;
                return this;
            }
            
            public TCPConnection onData(Consumer<String> jso) {
                onData = jso;
                return this;
            }
            
            public TCPConnection onDisconnect(Consumer<Boolean> jso) {
                onDisconnect = jso;
                return this;
            }
            
            public boolean hasConnection() {
                return hasConnection;
            }
            
            public int getPort() {
                return port;
            }
            
            public String getRemoteIP() {
                return otherEnd.remoteIP;
            }
            
            public void disconnectByRemote() {
                disconnectHelper(true);
            }
            
            public void disconnect() {
                disconnectHelper(false);
                otherEnd.object.disconnectByRemote();
            }
            
            private void disconnectHelper(boolean byRemote) {
                otherEnd.remoteIP = null;
                hasConnection = false;
                if(onDisconnect != null) {
                    Node.this.getNet().getExecutor().submit(() -> {
                        onDisconnect.accept(byRemote);
                    });
                }
            }
            
            //For clients
            public void setOtherEnd(TCPConnection otherEnd, String remoteIP, int port) {
                if(isServer) {
                    return;
                }
                this.otherEnd.port = port;
                this.otherEnd.remoteIP = remoteIP;
                this.otherEnd.object = otherEnd;
            }
            
            //For clients
            public boolean connect(String remoteIP, int port) {
                if(isServer) {
                    return false;
                }
                hasConnection = true;
                //System.out.println("Connecting to: " + remoteIP + ":" + port);
                
                if(onConnection != null) {
                    Node.this.getNet().getExecutor().submit(() -> {
                        onConnection.accept(remoteIP);
                    });
                }
                return true;
            }
            
            //For servers
            public TCPConnection connect(String remoteIP, int port, TCPConnection otherEnd) {
                if(port != port || hasConnection || !isServer) {
                    return null;
                }
                otherEnd.setOtherEnd(this, Node.this.getIP(), port);
                TCPConnectionData data = new TCPConnectionData();
                data.remoteIP = remoteIP;
                data.port = port;
                data.object = otherEnd;
                clients.add(data);
                hasConnection = true;
                if(onConnection != null) {
                    Node.this.getNet().getExecutor().submit(() -> {
                        onConnection.accept(remoteIP);
                    });
                }
                return this;
            }
            
            public void receiveData(String data) {
                if(onData != null) {
                    Node.this.getNet().getExecutor().submit(() -> {
                        onData.accept(data);
                    });
                }
            }
        }
    }
    public class UDP {
        private ReceiveFunction rf;
        
        public void send(String ip, int port, String data) {
            net.sendUDPData(ip, port, data, Node.this.getIP());
        }
        public void receive(String ip, int port, String data) {
            //System.out.println("Receiving from: " + ip + ":" + port + " data: " + data);
            if(rf != null) {
                Node.this.getNet().getExecutor().submit(() -> {
                    rf.accept(ip, port, data);
                });
            }
        }
        public void onReceive(ReceiveFunction rf) {
            this.rf = rf;
        }
    }
    public class NodeUtility {
        public String getIP() {
            return ip;
        }
        
        public String getBroadcastAddr() {
            return net.getBroadcastAddr();
        }
        
        public boolean pingIP(String ip) {
            return Node.this.getNet().ping(ip);
        }
        
        public java.util.Timer wait(TimerTask runnable, int milliseconds) {
            java.util.Timer timer = new java.util.Timer();
            timer.schedule(runnable, milliseconds);
            return timer;
        }
    }
    
    public Node(Consumer<ScriptException> errorCallback, String ip, Network net, Computer computer) {
        this.errorCallback = errorCallback;
        this.ip = ip;
        this.net = net;
        this.computer = computer;
    }
    
    public void setIP(String ip) {
        this.ip = ip;
    }
    
    public String getIP() {
        return ip;
    }
    
    public UDP getUDP() {
        return udp;
    }
    
    public TCP getTCP() {
        return tcp;
    }
    
    public synchronized Network getNet() {
        return net;
    }
    
    public void setScript(String script) {
        this.script = script;
        
        initCommunications();
        evalScript();
    }
    
    private void initCommunications() {
        tcp = new TCP();
        udp = new UDP();
        nodeutility = new NodeUtility();
    }
    
    private void evalScript() {
        working = false;
        engine.put("TCP", tcp);
        engine.put("UDP", udp);
        engine.put("Utility", nodeutility);
        getNet().getExecutor().submit(() -> {
            try {
                engine.eval(script);
            } catch(ScriptException se) {
                errorCallback.accept(se);
            }
        });
        inv = (Invocable)engine;
        working = true;
    }
    
    public boolean isWorking() {
        return working;
    }
}