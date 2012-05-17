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
        
    public void append(StreamEntry entry) 
    {        
        super.append(entry);

        synchronized (this) {
            ByteBuffer tmp_buffer = ByteBuffer.allocate(buffer.capacity() + entry.buffer.capacity());
            tmp_buffer.put(buffer);
            tmp_buffer.put(entry.buffer);
            buffer = tmp_buffer;
            logger.debug(connectionId + " " + String.valueOf(buffer.capacity()));
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
