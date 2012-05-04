/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.redeye.UnsecureWLAN;

import java.util.Collection;
import org.apache.log4j.Logger;

/**
 *
 * @author root
 */
public class PacketAnalyzer extends Thread {
    
    private static final Logger logger = Logger.getLogger(PacketAnalyzer.class);
    
    StreamHandler handler;
    boolean do_stop;
    
    public PacketAnalyzer(StreamHandler handler)
    {
        this.handler = handler;
        do_stop = false;
    }
    
    @Override
    public void run()
    {
        while( !do_stop ) {        
            Collection<StreamEntry> entries = handler.getEntries();
            
            for( StreamEntry entry : entries )
            {
               if( dump_string( entry) ) {
                   
               }
            }
        }
    }
    
    public boolean dump_string( StreamEntry entry ) 
    {
        try {
            String data = new String( entry.getByteArray(), "UTF-8" );
            
            if( data.charAt(0) < 127 && data.charAt(0) > 32 ) {            
                logger.debug("data: " + data);            
                return true;
            }
        } catch( Exception ex ) {
            return false;
        }
        
        return false;
    }
    
    public void doStop()
    {
        do_stop = true;
    }
    
}
