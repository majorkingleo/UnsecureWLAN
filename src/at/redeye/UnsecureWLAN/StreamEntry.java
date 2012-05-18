/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.redeye.UnsecureWLAN;

import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import org.jnetpcap.protocol.network.Ip4;
import org.jnetpcap.protocol.tcpip.Tcp;

/**
 *
 * @author root
 */
public class StreamEntry extends StreamEntryWithoutContent {
               
    ByteBuffer buffer;  
    
    public StreamEntry( Ip4 ipv4, Tcp tcp ) throws UnknownHostException
    {
        super(ipv4,tcp);
        
        byte bytes[] = tcp.getPayload();
        /*
        if( bytes.length == 0)  {               
            logger.debug("lenght: " + tcp.getLength() + " offset: " + tcp.getPayloadOffset());                        
            bytes = tcp.getByteArray(0,tcp.getLength());
            logger.debug("ok");                                  
        }*/
        
        if( bytes.length > 0 ) {
            buffer = ByteBuffer.wrap(bytes);
        } 
    }
            
    @Override
    public void append(StreamEntryWithoutContent entry) {
        super.append(entry);

        if (entry instanceof StreamEntry) {
            StreamEntry sentry = (StreamEntry) entry;

            synchronized (this) {
                ByteBuffer tmp_buffer = ByteBuffer.allocate(buffer.limit() + sentry.buffer.limit());
                buffer.position(0);
                tmp_buffer.put(buffer);
                sentry.buffer.position(0);
                tmp_buffer.put(sentry.buffer);
                buffer = tmp_buffer;
                buffer.position(0);
//                logger.debug(connectionId + " cap: " + String.valueOf(buffer.capacity()));
            }
        }
    }
    
    public byte[] getByteArray()
    {
        synchronized( this ) {
            if( buffer == null )
                return null;
            
            return buffer.array();
        }
    }
}
