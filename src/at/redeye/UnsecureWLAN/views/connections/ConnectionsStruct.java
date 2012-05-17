/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.redeye.UnsecureWLAN.views.connections;

import at.redeye.FrameWork.base.bindtypes.DBDateTime;
import at.redeye.FrameWork.base.bindtypes.DBString;
import at.redeye.FrameWork.base.bindtypes.DBStrukt;

/**
 *
 * @author root
 */
public class ConnectionsStruct extends DBStrukt 
{
    DBString ip_from = new DBString("ip_from", "Von", 50);
    DBString ip_to = new DBString("ip_from", "Nach", 50);
    DBString connection_id = new DBString("connection_id", 50 );
    DBDateTime last_modification = new DBDateTime("last_modification", "Letzte Änderung");
    DBDateTime creation_time = new DBDateTime("created_at", "Startzeitpunkt");
    
    public ConnectionsStruct()
    {
        super("CONNECTIONS");
        
        add(ip_from);
        add(ip_to);
        add(connection_id);
        add(creation_time);
        add(last_modification);        
    }

    @Override
    public DBStrukt getNewOne() {
        return new ConnectionsStruct();
    }
            
}
