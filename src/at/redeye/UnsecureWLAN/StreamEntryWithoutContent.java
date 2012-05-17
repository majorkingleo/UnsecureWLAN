/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.redeye.UnsecureWLAN;

import java.net.InetAddress;
import java.net.UnknownHostException;
import org.apache.log4j.Logger;
import org.jnetpcap.protocol.network.Ip4;
import org.jnetpcap.protocol.tcpip.Tcp;

/**
 *
 * @author root
 */
public class StreamEntryWithoutContent {
    
    static final Logger logger = Logger.getLogger(StreamEntryWithoutContent.class);
     
    String connectionId;
    long creation_time;
    long update_time;
    String dest_ip;
    String source_ip;
    int dest_port;
    int source_port;
    
    public StreamEntryWithoutContent( Ip4 ipv4, Tcp tcp ) throws UnknownHostException
    {
        dest_ip = InetAddress.getByAddress(ipv4.destination()).toString();
        source_ip = InetAddress.getByAddress(ipv4.source()).toString();
        
        connectionId = String.format("%s:%d - %s:%d", 
                dest_ip, tcp.destination(),
                source_ip, tcp.source());              
        
        dest_port = tcp.destination();
        source_port = tcp.source();
        
        update_time = creation_time = System.currentTimeMillis();
    }
    
    @Override
    public String toString() {
        return connectionId;
    }
    
    public String getConnectionId() {
        return connectionId;
    }
    
    public void append(StreamEntryWithoutContent entry) 
    {       
        update_time = entry.creation_time;
    }
    
    public String getDestIp() {
        return dest_ip;
    }
    
    public String getSourceIp() {
        return source_ip;
    }

    public int getDestPort() {
        return dest_port;
    }
    
    public int getSourcePort() {
        return source_port;
    }    
    
    public long getCreationTime() {
        return creation_time;
    }
    
    public long getModificationTime() {
        return update_time;
    }
}
