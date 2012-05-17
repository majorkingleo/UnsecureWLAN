package at.redeye.UnsecureWLAN.views.browser;

import at.redeye.UnsecureWLAN.StreamHandler;
import org.apache.log4j.Logger;
import org.jnetpcap.protocol.network.Ip4;
import org.jnetpcap.protocol.tcpip.Tcp;

/**
 *
 * @author root
 */
public class BrowserHandler extends StreamHandler 
{
    private static final Logger logger = Logger.getLogger(BrowserHandler.class);
    
    public BrowserHandler()
    {
        
    }
            
    @Override
    public boolean wantPacket( Ip4 ip4, Tcp tcp )
    {       
        if( (tcp.destination() == 80) ||
            (tcp.source() == 80) ) {
            return true;
        }
        
        return false;
    }    

}
