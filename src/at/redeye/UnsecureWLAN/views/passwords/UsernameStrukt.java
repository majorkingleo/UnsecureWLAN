/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.redeye.UnsecureWLAN.views.passwords;

import at.redeye.FrameWork.base.bindtypes.DBDateTime;
import at.redeye.FrameWork.base.bindtypes.DBString;
import at.redeye.FrameWork.base.bindtypes.DBStrukt;

/**
 *
 * @author root
 */
public class UsernameStrukt extends DBStrukt
{
    public DBString ip_from = new DBString( "source_ip", "Von", 50 );
    public DBString address = new DBString( "address", "URL", 255);
    public DBString connection_id = new DBString("connection_id", 50 );
    public DBString username = new DBString("username", 50 );
    public DBString password = new DBString("password", 50 );
    public DBDateTime creation_time = new DBDateTime("created_at", "Startzeitpunkt");

    public UsernameStrukt()
    {
        super("USERNAMES");
        
        add( ip_from );
        add( address );
        add( connection_id );
        add( username );
        add( password );
        add( creation_time );
    }
    
    @Override
    public DBStrukt getNewOne() {
        return new UsernameStrukt();
    }
    
    
}
