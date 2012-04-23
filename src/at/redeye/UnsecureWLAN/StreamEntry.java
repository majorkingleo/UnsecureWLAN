/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.redeye.UnsecureWLAN;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import org.apache.log4j.Logger;
import org.jnetpcap.protocol.network.Ip4;
import org.jnetpcap.protocol.tcpip.Tcp;

/**
 *
 * @author root
 */
public class StreamEntry {
    
    private static final Logger logger = Logger.getLogger(StreamEntry.class);
    
    private String connectionId;
    ByteBuffer buffer;
    
    
    public StreamEntry( Ip4 ipv4, Tcp tcp ) throws UnknownHostException
    {
        final String dest = InetAddress.getByAddress(ipv4.destination()).toString();
        final String source = InetAddress.getByAddress(ipv4.destination()).toString();
        
        connectionId = String.format("%s:%d - %s:%d", 
                dest, tcp.destination(),
                source, tcp.source());   
        
        tcp.getLength();
        
        byte bytes[] = tcp.getByteArray(0, tcp.size());
        buffer = ByteBuffer.wrap(bytes);
    }
    
    @Override
    public String toString()
    {
        return connectionId;
    }

    public void append(StreamEntry entry) 
    {
        ByteBuffer tmp_buffer = ByteBuffer.allocate(buffer.capacity() + entry.buffer.capacity());
        tmp_buffer.put(buffer);
        tmp_buffer.put(entry.buffer);
        buffer = tmp_buffer;
        logger.debug(connectionId + " " + String.valueOf(buffer.capacity()));
    }
}
