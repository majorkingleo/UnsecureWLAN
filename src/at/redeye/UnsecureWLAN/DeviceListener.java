/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.redeye.UnsecureWLAN;

import at.redeye.FrameWork.base.Setup;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapAddr;
import org.jnetpcap.PcapIf;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;
import org.jnetpcap.protocol.lan.Ethernet;
import org.jnetpcap.protocol.network.Ip4;
import org.jnetpcap.protocol.tcpip.Tcp;

/**
 *
 * @author moberza
 */
public class DeviceListener extends Thread
{
    protected static final Logger logger = Logger.getLogger(DeviceListener.class);
    PcapIf device;
    StringBuilder errbuf = new StringBuilder();
    PcapPacketHandler<String> jpacketHandler;
    boolean do_stop = false;
    Pcap pcap;
    MainWin mainwin;
    final ArrayList<StreamHandler> handlers = new ArrayList<StreamHandler>();    
    
    public DeviceListener(PcapIf device, final MainWin mainwin) {
        super(getName(device));

        this.mainwin = mainwin;
        this.device = device;
        

        if( device != null )
            logger.debug("device " + device.getName());
        
        init();
    }   
    
    final void init()
    {
        final Thread sender = this;

        jpacketHandler = new PcapPacketHandler<String>() {

            @Override
            public void nextPacket(PcapPacket packet, String user) {
               
                // logger.debug("nextPacket " + user);
                
                Ethernet eth = null;                
                Ip4 ipv4 = null;
                
                try {
                    eth = packet.getHeader( new Ethernet());
                    ipv4 = packet.getHeader( new Ip4() );
                } catch( IndexOutOfBoundsException ex ) {
                    logger.debug("unknown packet",ex);
                    return;
                }  
                
                if( eth == null || ipv4 == null )
                    return;                                                

               Tcp tcp = packet.getHeader(new Tcp());

                if (tcp != null) {

                    synchronized (handlers) {
                        for (StreamHandler handler : handlers) {
                            try {
                                // logger.trace("here");
                                if (handler.wantPacket(ipv4, tcp)) {
                                    handler.eatPacket(ipv4, tcp);
                                }
                            } catch (Exception ex) {
                                logger.debug(ex,ex);
                            }
                        } // for
                    }
                } // if
                
            } // next Packet
        };
    }
    
    public static Long ipToInt(String addr) {

        String[] addrArray = addr.split("\\.");

        long num = 0;

        for (int i = 0; i < addrArray.length; i++) {
            int power = 3 - i;
            num += ((Integer.parseInt(addrArray[i]) % 256 * Math.pow(256, power)));
        }

        return num;
    }
    
    public static String intToIp(int i) {

        return ((i >> 24) & 0xFF) + "."
                + ((i >> 16) & 0xFF) + "."
                + ((i >> 8) & 0xFF) + "."
                + (i & 0xFF);
    }
    
    public static long unsignedIntToLong(byte[] b) {
        long l = 0;
        l |= b[0] & 0xFF;
        l <<= 8;
        l |= b[1] & 0xFF;
        l <<= 8;
        l |= b[2] & 0xFF;
        l <<= 8;
        l |= b[3] & 0xFF;
        return l;
    }         
    
    public Pcap openDevice() {

        if (device != null) {
            logger.debug("starting device listener on " + getName() + " " + device.getName());

            int snaplen = 64 * 1024;           // Capture all packets, no trucation  
            int flags = Pcap.MODE_PROMISCUOUS; // capture all packets  
            int timeout = 500;
            Pcap pcap_device = Pcap.openLive(device.getName(), snaplen, flags, timeout, errbuf);

            return pcap_device;
        }

        return null;
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
        
        while( !do_stop ) {            
            
            try {                
                if( pcap.loop(0, jpacketHandler, getName()) < 0 ) {
                    break;
                }
            } catch( Exception ex ) {
                logger.error(ex);
            }
                
            if( do_stop )
                break;            
        }
        
        logger.debug("Stopping devicelistener" );
        pcap.close();
    }
    
    void doStop()
    {
        do_stop = true;       
        
        if( pcap != null ) {
            pcap.breakloop();   
        }
    }
    
    public static String getName(PcapIf device ) {
                                
        if( device == null )
            return "";
        
        String descr = device.getDescription();
        
        if( Setup.is_linux_system() )
            descr = device.getName();
        
        if( descr == null )
            descr = "";
        
        List<PcapAddr> addresses = device.getAddresses();
        
        int size = addresses.size();
        
        String better_descr = null;
        
        for( PcapAddr addr :  addresses )
        {           
            if(  addr.getAddr().getFamily() == 2 )
                better_descr = addr.getAddr().toString();
            
            logger.debug(addr.getAddr().toString() + " " +  addr.getAddr().getFamily());
        }
        
        if( better_descr != null )
            descr += " " + better_descr;
        
        return descr;
    }

    void addHandler(StreamHandler handler) {
        synchronized (handlers) {
            handlers.add(handler);
        }
    }
    
    void removeHandler(StreamHandler handler) {
        synchronized (handlers) {
            handlers.remove(handler);
        }
    }
}