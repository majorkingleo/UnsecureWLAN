package at.redeye.UnsecureWLAN.views.connections;

import at.redeye.UnsecureWLAN.StreamEntryWithoutContent;
import at.redeye.UnsecureWLAN.StreamHandler;
import java.net.UnknownHostException;
import org.apache.log4j.Logger;
import org.jnetpcap.protocol.network.Ip4;
import org.jnetpcap.protocol.tcpip.Tcp;

/**
 *
 * @author root
 */
public class ConnectionHandler extends StreamHandler 
{
    private static final Logger logger = Logger.getLogger(ConnectionHandler.class);
    
    public ConnectionHandler()
    {
        
    }
            
    @Override
    public boolean wantPacket( Ip4 ip4, Tcp tcp )
    {       
        return true;
    }    
    
    @Override
    public StreamEntryWithoutContent eatPacket( Ip4 ip4, Tcp tcp ) throws UnknownHostException
    {
        StreamEntryWithoutContent entry = new StreamEntryWithoutContent(ip4, tcp);
               
        StreamEntryWithoutContent existing = get(entry.toString());
        
        if( existing == null ) {
            put(entry.toString(),entry);            
            logger.debug("new connection detected: " + entry.toString() );
            return entry;
        } else {
            existing.append(entry); 
            return existing;
        }
    }
}
