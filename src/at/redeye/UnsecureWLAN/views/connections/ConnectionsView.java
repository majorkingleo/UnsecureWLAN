/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.redeye.UnsecureWLAN.views.connections;

import at.redeye.FrameWork.base.BaseDialog;
import at.redeye.FrameWork.base.Root;
import at.redeye.FrameWork.base.tablemanipulator.TableManipulator;
import at.redeye.UnsecureWLAN.MainWin;
import at.redeye.UnsecureWLAN.StreamEntryWithoutContent;
import java.util.Date;
import java.util.HashMap;
import java.util.TimerTask;
import java.util.Vector;

/**
 *
 * @author root
 */
public class ConnectionsView extends BaseDialog {

    Vector<ConnectionsStruct> connections = new Vector();
    TableManipulator tm;
    MainWin main_win;
    ConnectionHandler handler;
    
    public ConnectionsView(Root root, MainWin main_win) {
        super(root, root.MlM("Verbindungen"));
        initComponents();
        
        this.main_win = main_win;
        
        ConnectionsStruct strukt = new ConnectionsStruct();
        tm = new TableManipulator(root, jTable1, strukt);
        
        tm.hide(strukt.connection_id);
        tm.prepareTable();
        
        handler = new ConnectionHandler();
        main_win.registerHandler( handler );

        getAutoRefreshTimer().schedule(new TimerTask() {

            @Override
            public void run() {
                java.awt.EventQueue.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        update();
                    }
                });
            }
        }, 300, 1000);
    }
    
    @Override
    public void close()
    {
        main_win.unregisterHandler(handler);
        super.close();
    }
    
    public void update() {                
        
        HashMap<String,StreamEntryWithoutContent> con_map = handler.getEntries();
        boolean added_something = false;
        
        for( StreamEntryWithoutContent entry : con_map.values() ) {
            boolean found = false;
            
            for( ConnectionsStruct strukt : connections ) {
                if( strukt.connection_id.getValue().equals(entry.getConnectionId()) ) {
                    
                    strukt.last_modification.loadFromCopy(new Date(entry.getModificationTime()));
                    added_something = true;
                    found = true;                    
                    break;
                }
            } // for
            
            if( !found ) {
                ConnectionsStruct strukt = new ConnectionsStruct();
                strukt.connection_id.loadFromString(entry.getConnectionId());
                strukt.ip_from.loadFromString(entry.getSourceIp() + ":" + entry.getSourcePort());
                strukt.ip_to.loadFromString(entry.getDestIp() + ":" + entry.getDestPort());
                strukt.last_modification.loadFromCopy(new Date(entry.getModificationTime()));
                strukt.creation_time.loadFromCopy(new Date(entry.getCreationTime()));
                
                added_something  = true;
                tm.add(strukt);                
                connections.add(strukt);
                logger.debug( "adding: " + entry.toString() );
            }
        }
        
        if( added_something )
            tm.updateUI();
    }
    
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
 
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables
}
