package at.redeye.UnsecureWLAN.views.lib;

import at.redeye.UnsecureWLAN.views.browser.*;
import at.redeye.UnsecureWLAN.StreamHandler;
import java.util.HashSet;
import org.apache.log4j.Logger;
import org.jnetpcap.protocol.network.Ip4;
import org.jnetpcap.protocol.tcpip.Tcp;

/**
 *
 * @author root
 */
public class PortHandler extends StreamHandler 
{
    private static final Logger logger = Logger.getLogger(PortHandler.class);        
    
    HashSet<Integer> ports;
    
    public PortHandler()
    {
        ports = new HashSet<Integer>();
    }
    
    public PortHandler( int ... port_numbers )
    {
        ports = new HashSet<Integer>();
        
        for( int port : port_numbers ) {
            ports.add(port);
        }
    }
            
    @Override
    public boolean wantPacket( Ip4 ip4, Tcp tcp )
    {   
        if( ports.contains(tcp.destination()) ||
            ports.contains(tcp.source() ) ) {
            return true;
        }
        
        return false;
    }    

}
