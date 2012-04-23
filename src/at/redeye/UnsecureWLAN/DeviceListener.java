/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.redeye.UnsecureWLAN;

import at.redeye.FrameWork.base.Setup;
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
    private static final Logger logger = Logger.getLogger(DeviceListener.class);
    PcapIf device;
    StringBuilder errbuf = new StringBuilder();
    PcapPacketHandler<String> jpacketHandler;
    boolean do_stop = false;
    Pcap pcap;
    MainWin mainwin;
    HTTPHandler handler = new HTTPHandler();
    
    public DeviceListener(PcapIf device, final MainWin mainwin) {
        super(getName(device));

        this.mainwin = mainwin;
        this.device = device;
        
       final Thread sender = this;

        jpacketHandler = new PcapPacketHandler<String>() {

            @Override
            public void nextPacket(PcapPacket packet, String user) {
               
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

                    try {
                        if (handler.wantPacket(ipv4, tcp)) {
                            handler.eatPacket(ipv4, tcp);
                        }
                    } catch (Exception ex) {
                        logger.debug(ex);
                    }
                }
                
            }
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
    
    @Override
    public void run()
    {
        int snaplen = 64 * 1024;           // Capture all packets, no trucation  
        int flags = Pcap.MODE_PROMISCUOUS; // capture all packets  
        int timeout = 500;           // 10 seconds in millis  
        pcap = Pcap.openLive(device.getName(), snaplen, flags, timeout, errbuf);

        if (pcap == null) {
            logger.error("Error while opening device for capture: "
                    + errbuf.toString());
            return;
        } 
        
        while( !do_stop ) {            
            
            try {
                pcap.loop(100, jpacketHandler, this.getName());
            } catch( Exception ex ) {
                logger.error(ex);
            }
                
            if( do_stop )
                break;            
        }
        
        pcap.close();
    }
    
    void doStop()
    {
        do_stop = true;
        pcap.breakloop();        
    }
    
    public static String getName(PcapIf device ) {
        
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
}