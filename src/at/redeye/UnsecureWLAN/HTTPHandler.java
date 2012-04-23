/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.redeye.UnsecureWLAN;

import org.jnetpcap.protocol.network.Ip4;
import org.jnetpcap.protocol.tcpip.Tcp;

/**
 *
 * @author root
 */
public class HTTPHandler extends StreamHandler
{
    public HTTPHandler()
    {
        
    }
    
    
    @Override
    public boolean wantPacket( Ip4 ip4, Tcp tcp )
    {
       if( tcp.destination() == 80 ||
           tcp.source() == 80 )
           return true;
       
       return false;
    }    
}
