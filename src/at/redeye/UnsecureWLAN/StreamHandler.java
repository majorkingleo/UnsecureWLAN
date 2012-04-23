/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.redeye.UnsecureWLAN;

import java.net.UnknownHostException;
import java.util.HashMap;
import org.apache.log4j.Logger;
import org.jnetpcap.protocol.network.Ip4;
import org.jnetpcap.protocol.tcpip.Tcp;

/**
 *
 * @author root
 */
public class StreamHandler {
    
    private static final Logger logger = Logger.getLogger(StreamHandler.class);
    
    HashMap<String,StreamEntry> entries = new HashMap();    
    
    public StreamHandler()
    {
        entries = new HashMap();    
    }
    
    public boolean wantPacket( Ip4 ip4, Tcp tcp )
    {
       return false;
    }
    
    public void eatPacket( Ip4 ip4, Tcp tcp ) throws UnknownHostException
    {
        StreamEntry entry = new StreamEntry(ip4, tcp);
        
        
        StreamEntry existing = entries.get(entry.toString());
        
        if( existing == null )
            entries.put(entry.toString(),entry);
        else
            existing.append(entry);
       
    }
}
