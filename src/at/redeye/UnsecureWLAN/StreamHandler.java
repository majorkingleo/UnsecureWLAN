/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.redeye.UnsecureWLAN;

import java.net.UnknownHostException;
import java.util.Collection;
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
    
    final HashMap<String,StreamEntry> entries = new HashMap();    
    
    public StreamHandler()
    {
       
    }
    
    public boolean wantPacket( Ip4 ip4, Tcp tcp )
    {
       return false;
    }
    
    public StreamEntry eatPacket( Ip4 ip4, Tcp tcp ) throws UnknownHostException
    {
        StreamEntry entry = new StreamEntry(ip4, tcp);
                
        synchronized (entries) {

            StreamEntry existing = entries.get(entry.toString());

            if (existing == null) {
                entries.put(entry.toString(), entry);
                return entry;
            } else {
                existing.append(entry);
                return existing;
            }
        }
    }
    
     Collection<StreamEntry> getEntries()
     {
         synchronized(entries) {
            return entries.values();
        }
     }
}
