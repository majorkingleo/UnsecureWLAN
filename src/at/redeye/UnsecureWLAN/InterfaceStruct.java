/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.redeye.UnsecureWLAN;

import at.redeye.FrameWork.base.bindtypes.DBFlagJaNein;
import at.redeye.FrameWork.base.bindtypes.DBInteger;
import at.redeye.FrameWork.base.bindtypes.DBString;
import at.redeye.FrameWork.base.bindtypes.DBStrukt;

/**
 *
 * @author moberza
 */
public class InterfaceStruct extends DBStrukt {

    DBString iface = new DBString("iface","Interface",25);
    DBString dev_name = new DBString( "dev_name", "Device Name", 25 );
    DBInteger sent = new DBInteger("sent","Gesendet");
    DBInteger recv = new DBInteger("rec","Empfangen");    
    DBFlagJaNein listen = new DBFlagJaNein("listen","Abhören");
    
    public InterfaceStruct()
    {
        super("InterfaceStruct");
        
        add(iface);
        add(dev_name);
        add(listen);
        add(sent);
        add(recv);
    }
    
    @Override
    public DBStrukt getNewOne() {
        return new InterfaceStruct();
    }
    
}
