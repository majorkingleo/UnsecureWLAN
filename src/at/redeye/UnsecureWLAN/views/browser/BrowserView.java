/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.redeye.UnsecureWLAN.views.browser;

import at.redeye.UnsecureWLAN.views.lib.HTTPGet;
import at.redeye.FrameWork.base.AutoMBox;
import at.redeye.FrameWork.base.BaseDialog;
import at.redeye.FrameWork.base.Root;
import at.redeye.FrameWork.base.tablemanipulator.TableManipulator;
import at.redeye.UnsecureWLAN.MainWin;
import at.redeye.UnsecureWLAN.StreamEntry;
import at.redeye.UnsecureWLAN.StreamEntryWithoutContent;
import java.util.Date;
import java.util.HashMap;
import java.util.TimerTask;
import java.util.Vector;

/**
 *
 * @author root
 */
public class BrowserView extends BaseDialog {

    MainWin main_win;
    TableManipulator tm;
    Port80Handler handler;
    Vector<BrowserStrukt> connections = new Vector();
    
    public BrowserView(Root root, MainWin main_win) {
        super(root, root.MlM("Browser"));
        initComponents();
        
        this.main_win = main_win;
        
        BrowserStrukt strukt = new BrowserStrukt();
        tm = new TableManipulator(root, jTable1, strukt);
        
        tm.hide(strukt.connection_id);
        tm.prepareTable();
        
        handler = new Port80Handler();
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
        
        HashMap<String,StreamEntryWithoutContent> con_map = handler.getEntriesAndClear();
        boolean added_something = false;
        
        for( StreamEntryWithoutContent entry : con_map.values() ) {
            boolean found = false;
            
            for( BrowserStrukt strukt : connections ) {
                if( strukt.connection_id.getValue().equals(entry.getConnectionId()) ) {                                        
                    found = true;                    
                    break;
                }
            } // for
            
            if( !found ) {
                StreamEntry e = (StreamEntry) entry;
                String data = null;
                
                try {
                    byte[] bytes = e.getByteArray();                    
                    if( bytes != null &&  bytes.length > 0) {
                        data  = new String(bytes, "ASCII" );
                    }
                } catch( Exception ex ) {
                    logger.debug(ex,ex);
                }
                
                if( data != null ) {                   
                    // logger.trace(data);
                    if( !data.startsWith("GET")) {
                        data = null;
                    } else {
                        logger.debug(data);
                        HTTPGet get = new HTTPGet(data);
                        data = get.getRequestUrl();
                    }
                }
                
                if (data != null) {
                    BrowserStrukt strukt = new BrowserStrukt();
                    strukt.connection_id.loadFromString(entry.getConnectionId());
                    strukt.ip_from.loadFromString(entry.getSourceIp());
                    strukt.creation_time.loadFromCopy(new Date(entry.getCreationTime()));
                    strukt.address.loadFromString(data);

                    added_something = true;
                    tm.add(strukt);
                    connections.add(strukt);
                    logger.debug("adding: " + entry.toString());
                }
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
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
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

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked

        int row = tm.getSelectedRow();

        if (row >= 0) {
            BrowserStrukt strukt = connections.get(row);
            main_win.tryOpenUrl(strukt.address.getValue());
        }

    }//GEN-LAST:event_jTable1MouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables
}
