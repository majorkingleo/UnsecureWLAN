/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.redeye.UnsecureWLAN;

import java.io.File;
import org.jnetpcap.Pcap;

/**
 *
 * @author root
 */
public class DeviceListenerFromFile extends DeviceListener 
{
    File file;
    
    public DeviceListenerFromFile( File file, final MainWin mainwin ) 
    {
        super(null, mainwin);
        setName(file.getName());
        
        this.file = file;
        
        logger.debug( "reading from file " + file );
    }
    
    
    @Override
    public Pcap openDevice() {
        logger.debug("starting device listener on " + file);
        
        Pcap pcap_device = Pcap.openOffline(file.getAbsolutePath(), errbuf);                       
        
        return pcap_device;
    }
    
    @Override
    public void run()
    {        
        pcap = openDevice();   

        if (pcap == null) {
            logger.error("Error while opening device for capture: "
                    + errbuf.toString());
            return;
        }                 
                                   
        try {
            pcap.loop(-1, jpacketHandler, getName());
                            
        } catch (Exception ex) {
            logger.error(ex);
        }


        logger.debug("Stopping devicelistener");
        pcap.close();
        pcap = null;
        
        mainwin.removeListener(this);
    }
        
        
}
