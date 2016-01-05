import java.util.*;
import java.util.function.*;
import java.util.concurrent.*;

class Network {
    private ConcurrentHashMap<String, Node> nodes = new ConcurrentHashMap<>();
    private static final String BROADCAST_ADDR = "255.255.255.255";
    private double UDPPacketLoss = 0.0;
    private Random rand = new Random();
    private ExecutorService executor = Executors.newCachedThreadPool();
    
    public synchronized boolean sendUDPData(String ip, int port, String data, String fromIP) {
        if(BROADCAST_ADDR.equals(ip)) {
            broadcastUDPData(port, data, fromIP);
        } else {
            Node n = nodes.get(ip);
            if(n == null) {
                return false;
            } else {
                n.getUDP().receive(fromIP, port, data);
            }
        }
        return true;
    }
    
    public synchronized ExecutorService getExecutor() {
        return executor;
    }
    
    public synchronized boolean placeNodeAtIP(String ip, Node node) {
        if(ip == null ||
           node == null ||
           !Utility.verifyIP(ip) ||
           nodes.containsKey(ip) ||
           ip.equals(BROADCAST_ADDR)) {
            return false;
        }
        nodes.put(ip, node);
        return true;
    }
    
    public synchronized Node getAtIP(String ip) {
        return nodes.get(ip);
    }
    
    public synchronized Node removeAtIP(String ip) {
        return nodes.remove(ip);
    }
    
    public synchronized boolean ping(String ip) {
        return nodes.containsKey(ip);
    }
    
    public synchronized boolean changeIP(String oldIP, String newIP) {
        if(oldIP == null ||
           newIP == null) {
            return false;
        }
        Node n = nodes.remove(oldIP);
        if(n == null) {
            return false;
        }
        n.setIP(newIP);
        return placeNodeAtIP(newIP, n);
    }
    
    public synchronized void setPacketLoss(final double loss) {
        UDPPacketLoss = loss;
    }
    
    private synchronized void broadcastUDPData(int port, String data, String fromIP) {
        for(Map.Entry<String, Node> entry : nodes.entrySet()) {
            if(rand.nextDouble() >= UDPPacketLoss) { //simulate packet loss
                entry.getValue().getUDP().receive(fromIP, port, data);
            }
        }
    }
    
    public synchronized ArrayList<Node.TCP.TCPConnection> connectToTCPServers(String ip, int port, String fromIP, Node.TCP.TCPConnection client) {
        ArrayList<Node.TCP.TCPConnection> retList = new ArrayList<>();
        if(BROADCAST_ADDR.equals(ip)) {
            for(Map.Entry<String, Node> entry : nodes.entrySet()) {
                Node.TCP.TCPConnection val = connectToTCPServerHelper(entry.getKey(), port, fromIP, client);
                if(val != null) {
                    retList.add(val);
                }
            }
        } else {
            Node.TCP.TCPConnection val = connectToTCPServerHelper(ip, port, fromIP, client);
            if(val != null) {
                retList.add(val);
            }
        }
        return retList;
    }
    
    private synchronized Node.TCP.TCPConnection connectToTCPServerHelper(String ip, int port, String fromIP, Node.TCP.TCPConnection client) {
        Node n = nodes.get(ip);
        if(n == null) {
            return null;
        }
        return n.getTCP().getServerAtPort(port).connect(fromIP, port, client);
    }
}
