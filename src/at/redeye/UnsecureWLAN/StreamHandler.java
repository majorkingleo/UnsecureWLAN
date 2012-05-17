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
    
    final HashMap<String,StreamEntryWithoutContent> entries = new HashMap<String,StreamEntryWithoutContent>();    

    
    public StreamHandler()
    {
    }
    
    public boolean wantPacket( Ip4 ip4, Tcp tcp )
    {
       return false;
    }
    
    public StreamEntryWithoutContent eatPacket( Ip4 ip4, Tcp tcp ) throws UnknownHostException
    {
        StreamEntry entry = new StreamEntry(ip4, tcp);        
        
        synchronized( entries )
        {
            StreamEntryWithoutContent existing = entries.get(entry.toString());
        
            if( existing == null ) {
                entries.put(entry.toString(),entry);
                return entry;
            } else {
                existing.append(entry);
                return existing;
            }                        
        }               
    }

    
    public StreamEntryWithoutContent get( String connectionId ) {
        return entries.get(connectionId);
    }
    
    public void put( String connectionId, StreamEntryWithoutContent entry ) {
        entries.put(connectionId, entry);
    }
    
    /**          
     * @return a copy of the internal hash map
     * Die StreamEntry an sich werde trotzdem noch immer befüllt.
     * Als die append() Methode wird nach wie vor aufgerufen.
     */
    public HashMap<String,StreamEntryWithoutContent>  getEntries()
    {
        HashMap<String,StreamEntryWithoutContent> res = new HashMap();
        
        synchronized( entries ) {
            res.putAll(entries);
        }
        
        return res;
    }
    
    /**          
     * @return a copy of the internal hash map
     * Die Interne Hashmap wird dabei geleert.
     */    
    public HashMap<String,StreamEntryWithoutContent>  getEntriesAndClear()
    {
        HashMap<String,StreamEntryWithoutContent> res = new HashMap();
        
        synchronized( entries ) {
            res.putAll(entries);
            entries.clear();
        }
        
        return res;
    }    

}
